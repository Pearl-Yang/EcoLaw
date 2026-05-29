/**
 * 表单验证与生命周期管理 Hook
 * 用于小程序端的表单处理
 */

import { ref, reactive, onUnmounted, onMounted } from 'vue'

/**
 * 通用表单验证器
 */
export const validators = {
  required: (val, message = '必填项') => {
    if (val === null || val === undefined || val === '') return message
    if (Array.isArray(val) && val.length === 0) return message
    return true
  },
  
  phone: (val) => {
    if (!val) return true // 非必填时不验证
    return /^1[3-9]\d{9}$/.test(val) || '手机号格式不正确'
  },
  
  smsCode: (val) => {
    if (!val) return true
    return /^\d{6}$/.test(val) || '请输入6位数字验证码'
  },
  
  password: (val) => {
    if (!val) return true
    if (val.length < 6) return '密码至少6位'
    if (val.length > 20) return '密码最多20位'
    return true
  },
  
  minLength: (val, length, message) => {
    if (!val) return true
    return (val.length || 0) >= length || message || `至少${length}位`
  },
  
  maxLength: (val, length, message) => {
    if (!val) return true
    return (val.length || 0) <= length || message || `最��${length}位`
  },
  
  pattern: (val, regex, message = '格式不正确') => {
    if (!val) return true
    return regex.test(val) || message
  },
  
  equal: (val, target, message = '两次输入不一致') => {
    return val === target || message
  }
}

/**
 * 表单 Hook
 * @param {Object} options 配置项
 * @param {Object} options.initialData 初始表单数据
 * @param {Function} options.onSubmit 提交回调
 * @param {Function} options.onValidate 自定义验证
 * @param {Object} options.rules 验证规则配置
 * @param {number} options.debounceTime 防抖时间(ms)
 */
export function useForm(options = {}) {
  const {
    initialData = {},
    onSubmit = async () => {},
    onValidate = () => true,
    rules = {},
    debounceTime = 300
  } = options

  // 状态
  const submitting = ref(false)
  const loading = ref(false)
  const formData = reactive({ ...initialData })
  const errors = reactive({})
  const touched = reactive({})
  
  // 防抖定时器
  let submitTimer = null
  let clearTimer = null

  // 初始化错误和触碰状态
  Object.keys(initialData).forEach(key => {
    errors[key] = ''
    touched[key] = false
  })

  /**
   * 验证单个字段
   */
  const validateField = (field, fieldRules) => {
    if (!fieldRules || !fieldRules.length) {
      errors[field] = ''
      return true
    }

    const value = formData[field]
    
    for (const rule of fieldRules) {
      let result = true
      let message = ''

      if (typeof rule === 'function') {
        result = rule(value)
        if (result !== true) message = result
      } else if (typeof rule === 'string') {
        result = validators[rule]?.(value)
        if (result !== true) message = result
      } else if (typeof rule === 'object') {
        // { required: true, message: 'xxx' } 或 { type: 'phone', message: 'xxx' }
        if (rule.required) {
          result = validators.required(value, rule.message || '必填项')
        } else if (rule.type) {
          const validatorFn = validators[rule.type]
          if (validatorFn) {
            const extraArgs = rule.params ? [value, ...rule.params] : [value]
            result = validatorFn(...extraArgs)
          }
        } else if (rule.validator && typeof rule.validator === 'function') {
          result = rule.validator(value, formData)
        }
        if (result !== true) message = result
      }

      if (result !== true) {
        errors[field] = message || '验证失败'
        return false
      }
    }

    errors[field] = ''
    return true
  }

  /**
   * ���证全部字段
   */
  const validate = () => {
    let valid = true
    for (const [field, fieldRules] of Object.entries(rules)) {
      touched[field] = true
      if (!validateField(field, fieldRules)) {
        valid = false
      }
    }
    
    // 自定义验证
    if (valid && typeof onValidate === 'function') {
      const customResult = onValidate(formData)
      if (customResult !== true) {
        valid = false
      }
    }
    
    return valid
  }

  /**
   * 处理失焦
   */
  const handleBlur = (field) => {
    touched[field] = true
    if (rules[field]) {
      validateField(field, rules[field])
    }
  }

  /**
   * 处理输入变化
   */
  const handleInput = (field, value) => {
    formData[field] = value
    // 已触碰且有规则时实时验证
    if (touched[field] && rules[field]) {
      validateField(field, rules[field])
    }
  }

  /**
   * 处理表单提交（带防抖）
   */
  const handleSubmit = async () => {
    // 标记所有字段为已触碰
    Object.keys(rules).forEach(key => {
      touched[key] = true
    })

    // 验证
    if (!validate()) {
      showToast('请检查表单填写')
      return false
    }

    // 防抖
    if (submitTimer) {
      clearTimeout(submitTimer)
    }

    return new Promise((resolve) => {
      submitTimer = setTimeout(async () => {
        submitting.value = true
        try {
          await onSubmit(formData)
          resolve(true)
        } catch (err) {
          showToast(err.message || '提交失败')
          resolve(false)
        } finally {
          submitting.value = false
        }
      }, debounceTime)
    })
  }

  /**
   * 立即提交（不带防抖，用于需要立即响应的场景）
   */
  const handleSubmitImmediately = async () => {
    Object.keys(rules).forEach(key => {
      touched[key] = true
    })

    if (!validate()) {
      showToast('请检查表单填写')
      return false
    }

    submitting.value = true
    try {
      await onSubmit(formData)
      return true
    } catch (err) {
      showToast(err.message || '提交失败')
      return false
    } finally {
      submitting.value = false
    }
  }

  /**
   * 重置表单
   */
  const reset = (newInitialData = {}) => {
    Object.assign(formData, { ...initialData, ...newInitialData })
    Object.keys(errors).forEach(key => {
      errors[key] = ''
    })
    Object.keys(touched).forEach(key => {
      touched[key] = false
    })
  }

  /**
   * 清空错误提示
   */
  const clearErrors = () => {
    Object.keys(errors).forEach(key => {
      errors[key] = ''
    })
  }

  /**
   * 设置字段值
   */
  const setFieldValue = (field, value) => {
    formData[field] = value
  }

  /**
   * 获取字段值
   */
  const getFieldValue = (field) => {
    return formData[field]
  }

  /**
   * 显示错误信息
   */
  const showError = (field, message) => {
    errors[field] = message
    touched[field] = true
  }

  /**
   * 清除单个字段错误
   */
  const clearFieldError = (field) => {
    errors[field] = ''
  }

  // 清理定时器
  onUnmounted(() => {
    if (submitTimer) clearTimeout(submitTimer)
    if (clearTimer) clearTimeout(clearTimer)
  })

  return {
    // 数据
    formData,
    errors,
    touched,
    
    // 状态
    submitting,
    loading,
    
    // 方法
    validate,
    validateField,
    handleBlur,
    handleInput,
    handleSubmit,
    handleSubmitImmediately,
    reset,
    clearErrors,
    setFieldValue,
    getFieldValue,
    showError,
    clearFieldError,
    
    // 验证器
    validators
  }
}

/**
 * 显示轻提示
 */
function showToast(title, icon = 'none') {
  uni.showToast({ title, icon, duration: 2000 })
}

export default useForm