<script setup>
/**
 * 通用表单弹窗组件
 * 
 * @description 提供完整的表单生命周期管理：
 * - 状态管理（loading、submitting）
 * - 表单验证
 * - 弹窗控制
 * - 错误处理
 * 
 * @example
 * <FormDialog
 *   v-model="visible"
 *   title="新增用户"
 *   :form-data="formData"
 *   :form-rules="rules"
 *   @submit="handleSubmit"
 * >
 *   <el-form-item label="用户名" prop="username">
 *     <el-input v-model="formData.username" />
 *   </el-form-item>
 * </FormDialog>
 */

import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  // 弹窗显示状态
  modelValue: {
    type: Boolean,
    default: false
  },
  
  // 弹窗标题
  title: {
    type: String,
    default: ''
  },
  
  // 弹窗宽度
  width: {
    type: String,
    default: '560px'
  },
  
  // 表单数据
  formData: {
    type: Object,
    default: () => ({})
  },
  
  // 表单验证规则
  formRules: {
    type: Object,
    default: () => ({})
  },
  
  // 是否在关闭时重置表单
  resetOnClose: {
    type: Boolean,
    default: true
  },
  
  // 是否显示提交按钮 loading
  loading: {
    type: Boolean,
    default: false
  },
  
  // 提交按钮文字
  submitText: {
    type: String,
    default: '确认'
  },
  
  // 取消按钮文字
  cancelText: {
    type: String,
    default: '取消'
  },
  
  // 是否禁用提交按钮
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'submit', 'close', 'opened'])

// ============ 1. 状态定义 ============
const formRef = ref()
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 是否为编辑模式（通过 formData.id 判断）
const isEdit = computed(() => !!props.formData?.id)

// 内部 loading 状态
const submitting = computed(() => props.loading)

// ============ 2. 生命周期：弹窗状态监听 ============
watch(visible, (val) => {
  if (val) {
    // 打开弹窗时清理验证状态
    nextTick(() => {
      formRef.value?.clearValidate()
      emit('opened')
    })
  } else {
    // 关闭弹窗
    if (props.resetOnClose) {
      formRef.value?.resetFields()
    }
    emit('close')
  }
})

// ============ 3. 方法 ============

/**
 * 表单验证
 */
const validate = () => formRef.value?.validate()

/**
 * 验证指定字段
 */
const validateField = (field) => formRef.value?.validateField(field)

/**
 * 清空验证
 */
const clearValidate = () => formRef.value?.clearValidate()

/**
 * 重置表单
 */
const resetFields = () => formRef.value?.resetFields()

/**
 * 设置字段值
 */
const setFieldValue = (field, value) => {
  if (props.formData) {
    props.formData[field] = value
  }
}

/**
 * 获取字段值
 */
const getFieldValue = (field) => props.formData?.[field]

/**
 * 关闭弹窗
 */
const close = () => {
  visible.value = false
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  try {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return false
    emit('submit', props.formData)
    return true
  } catch (err) {
    if (err !== false) {
      ElMessage.error('提交失败')
    }
    return false
  }
}

/**
 * 取消按钮点击
 */
const handleCancel = () => {
  visible.value = false
}

// ============ 4. 暴露给父组件 ============
defineExpose({
  formRef,
  validate,
  validateField,
  clearValidate,
  resetFields,
  setFieldValue,
  getFieldValue,
  close
})
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="width"
    class="form-dialog"
    :close-on-click-modal="false"
    :append-to-body="true"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      :disabled="disabled || submitting"
      class="form-dialog__form"
    >
      <slot />
    </el-form>
    
    <template #footer>
      <div class="form-dialog__footer">
        <el-button 
          @click="handleCancel" 
          :disabled="submitting"
          class="form-dialog__btn form-dialog__btn--cancel"
        >
          {{ cancelText }}
        </el-button>
        <el-button 
          type="primary" 
          :loading="submitting"
          :disabled="disabled"
          @click="handleSubmit"
          class="form-dialog__btn form-dialog__btn--submit"
        >
          {{ submitting ? '提交中...' : submitText }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss">
// 表单弹窗样式
.form-dialog {
  border-radius: 20px !important;
  overflow: hidden;
  
  .el-dialog {
    border-radius: 20px !important;
    background: rgba(255, 255, 255, 0.95) !important;
    backdrop-filter: blur(20px) !important;
    border: 1px solid rgba(82, 183, 136, 0.2) !important;
    box-shadow: 0 20px 60px rgba(26, 77, 46, 0.15) !important;
  }
  
  .el-dialog__header {
    border-bottom: 1px solid rgba(82, 183, 136, 0.1);
    padding: 20px 24px !important;
    margin: 0 !important;
  }
  
  .el-dialog__title {
    font-weight: 700;
    color: #1a4d2e;
    font-size: 18px;
  }
  
  .el-dialog__close {
    color: #9ca3af;
    font-size: 18px;
    
    &:hover {
      color: #1a4d2e;
    }
  }
  
  .el-dialog__body {
    padding: 24px !important;
    max-height: 60vh;
    overflow-y: auto;
  }
  
  .el-dialog__footer {
    border-top: 1px solid rgba(82, 183, 136, 0.1);
    padding: 16px 24px !important;
    margin: 0 !important;
  }
  
  &__form {
    // 表单样式调整
    .el-form-item {
      margin-bottom: 20px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
    
    .el-form-item__label {
      color: #1a4d2e;
      font-weight: 500;
    }
    
    .el-input__wrapper,
    .el-textarea__inner {
      background: rgba(255, 255, 255, 0.8) !important;
      border: 1px solid rgba(82, 183, 136, 0.2) !important;
      box-shadow: none !important;
      
      &:hover {
        border-color: #52b788;
      }
      
      &.is-focus {
        border-color: #52b788 !important;
        box-shadow: 0 0 0 2px rgba(82, 183, 136, 0.1) !important;
      }
    }
    
    .el-select {
      width: 100%;
    }
  }
  
  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
  
  &__btn {
    min-width: 100px;
    
    &--cancel {
      background: rgba(255, 255, 255, 0.7);
      color: #666;
      border: 1px solid rgba(0, 0, 0, 0.1);
      
      &:hover {
        background: rgba(0, 0, 0, 0.05);
      }
      
      &:disabled {
        opacity: 0.5;
      }
    }
    
    &--submit {
      background: linear-gradient(135deg, #52b788, #2d6a4f) !important;
      border: none !important;
      color: #fff !important;
      
      &:hover:not(:disabled) {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
      }
      
      &:disabled {
        opacity: 0.7;
      }
    }
  }
}

// 表单验证错误样式
.form-dialog__form {
  .el-form-item.is-error {
    .el-input__wrapper,
    .el-textarea__inner {
      border-color: #ef4444 !important;
    }
  }
  
  .el-form-item__error {
    color: #ef4444;
    font-size: 12px;
    padding-top: 4px;
  }
}
</style>
