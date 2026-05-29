/**
 * 报表导出工具
 * 支持 Excel 和 PDF 格式导出
 */
import * as XLSX from 'xlsx'
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

/**
 * 导出配置
 */
export const ExportConfig = {
  // Excel 配置
  excel: {
    bookType: 'xlsx',
    cellWidth: 120,
    headerHeight: 40
  },
  // PDF 配置
  pdf: {
    orientation: 'landscape',
    unit: 'mm',
    format: 'a4'
  }
}

/**
 * 颜色配置 - 黑白灰为主，绿色渐变
 */
export const ExportColors = {
  // 主色系
  primary: '#1a4d2e',      // 深绿
  secondary: '#2d6a4f',   // 中绿
  accent: '#52b788',      // 亮绿
  light: '#95d5b2',       // 浅绿
  
  // 黑白灰
  black: '#1a1a1a',
  darkGray: '#333333',
  gray: '#666666',
  lightGray: '#f5f5f5',
  white: '#ffffff',
  
  // 透明渐变
  gradientStart: 'rgba(26, 77, 46, 0.9)',
  gradientEnd: 'rgba(82, 183, 136, 0.7)'
}

/**
 * 生成导出文件名
 * @param {string} prefix - 文件名前缀
 * @param {string} format - 导出格式
 */
export function generateExportFilename(prefix, format = 'xlsx') {
  const now = new Date()
  const dateStr = now.toISOString().split('T')[0]
  const timeStr = now.toTimeString().split(' ')[0].replace(/:/g, '')
  return `${prefix}_${dateStr}_${timeStr}.${format}`
}

/**
 * 将数据转换为 Excel 工作表
 * @param {Array} headers - 表头
 * @param {Array} data - 数据
 * @param {Object} options - 配置选项
 */
export function dataToWorksheet(headers, data, options = {}) {
  const { merges = [], widths = {} } = options
  
  // 创建工作表数据
  const wsData = []
  
  // 添加表头
  wsData.push(headers.map(h => ({
    v: h.label,
    t: 's',
    s: {
      font: { bold: true, color: { rgb: ExportColors.white } },
      fill: { fgColor: { rgb: ExportColors.primary } },
      alignment: { horizontal: 'center', vertical: 'center' }
    }
  })))
  
  // 添加数据行
  data.forEach((row, rowIndex) => {
    const rowData = headers.map((h, colIndex) => {
      let value = row[h.key]
      let cellStyle = {
        alignment: { horizontal: 'center', vertical: 'center' },
        font: { color: { rgb: ExportColors.darkGray } }
      }
      
      // 序号处理
      if (h.key === '_index') {
        value = rowIndex + 1
      }
      
      // 格式化数据
      if (h.formatter && typeof h.formatter === 'function') {
        value = h.formatter(value, row)
      }
      
      // 百分比样式
      if (h.type === 'percent') {
        cellStyle.fill = { fgColor: { rgb: ExportColors.lightGray } }
        if (typeof value === 'number') {
          const bgColor = value >= 80 ? ExportColors.accent : 
                          value >= 50 ? '#f59e0b' : '#ef4444'
          cellStyle.font.color = { rgb: bgColor }
        }
      }
      
      // 状态标签样式
      if (h.type === 'status') {
        const statusColors = {
          '合规': ExportColors.accent,
          '待整改': '#f59e0b',
          '严重': '#ef4444'
        }
        cellStyle.font.color = { rgb: statusColors[value] || ExportColors.gray }
      }
      
      return {
        v: value,
        t: typeof value === 'number' ? 'n' : 's',
        s: cellStyle
      }
    })
    wsData.push(rowData)
  })
  
  // 创建工作表
  const ws = XLSX.utils.aoa_to_sheet(wsData)
  
  // 设置列宽
  ws['!cols'] = headers.map(h => ({
    wch: widths[h.key] || ExportConfig.excel.cellWidth / 10
  }))
  
  // 设置合并单元格
  if (merges.length > 0) {
    ws['!merges'] = merges
  }
  
  return ws
}

/**
 * 导出为 Excel 文件
 * @param {Array} headers - 表头配置
 * @param {Array} data - 数据
 * @param {string} filename - 文件名
 * @param {Object} options - 额外选项
 */
export async function exportToExcel(headers, data, filename, options = {}) {
  try {
    const ws = dataToWorksheet(headers, data, options)
    
    // 创建工作簿
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, options.sheetName || 'Sheet1')
    
    // 导出文件
    const wbout = XLSX.write(wb, { 
      bookType: 'xlsx', 
      type: 'array' 
    })
    
    downloadBlob(new Blob([wbout], { type: 'application/octet-stream' }), filename)
    
    return { success: true, filename }
  } catch (error) {
    console.error('Excel export error:', error)
    return { success: false, error: error.message }
  }
}

/**
 * 导出为 PDF 文件
 * @param {HTMLElement|string} element - DOM元素或选择器
 * @param {string} filename - 文件名
 * @param {Object} options - 配置选项
 */
export async function exportToPDF(element, filename, options = {}) {
  try {
    const targetEl = typeof element === 'string' 
      ? document.querySelector(element)
      : element
    
    if (!targetEl) {
      throw new Error('Target element not found')
    }
    
    const { 
      orientation = 'landscape',
      unit = 'mm',
      format = 'a4'
    } = options
    
    // 使用 html2canvas 转换为图片
    const canvas = await html2canvas(targetEl, {
      scale: 2,
      useCORS: true,
      backgroundColor: '#ffffff'
    })
    
    const imgData = canvas.toDataURL('image/png')
    const imgWidth = canvas.width
    const imgHeight = canvas.height
    
    // 计算 PDF 尺寸
    const pdf = new jsPDF(orientation, unit, format)
    const pdfWidth = pdf.internal.pageSize.getWidth()
    const pdfHeight = pdf.internal.pageSize.getHeight()
    
    // 计算缩放比例
    const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight) * 0.9
    const scaledWidth = imgWidth * ratio
    const scaledHeight = imgHeight * ratio
    
    // 居中放置
    const x = (pdfWidth - scaledWidth) / 2
    const y = (pdfHeight - scaledHeight) / 2
    
    // 添加标题
    pdf.setFont(ExportColors.primary)
    pdf.setFontSize(16)
    pdf.text(options.title || '环境法治数据报表', pdfWidth / 2, 10, { align: 'center' })
    
    // 添加日期
    pdf.setFontSize(10)
    pdf.setTextColor(ExportColors.gray)
    pdf.text(`导出时间: ${new Date().toLocaleString()}`, pdfWidth / 2, 18, { align: 'center' })
    
    // 添加图片
    pdf.addImage(imgData, 'PNG', x, y + 10, scaledWidth, scaledHeight)
    
    // 添加页脚
    pdf.setFontSize(8)
    pdf.setTextColor(ExportColors.gray)
    pdf.text(
      `绿法通 - 环境法治宣传与监管平台`,
      pdfWidth / 2, 
      pdfHeight - 5, 
      { align: 'center' }
    )
    
    // 下载 PDF
    pdf.save(filename)
    
    return { success: true, filename }
  } catch (error) {
    console.error('PDF export error:', error)
    return { success: false, error: error.message }
  }
}

/**
 * 下载 Blob 文件
 * @param {Blob} blob - Blob 对象
 * @param {string} filename - 文件名
 */
export function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

/**
 * 导出任务数据
 */
export async function exportTaskReport(data, filename) {
  const headers = [
    { key: '_index', label: '序号' },
    { key: 'org', label: '组织', width: 160 },
    { key: 'total', label: '任务总数', width: 100 },
    { key: 'done', label: '已完成', width: 100 },
    { key: 'rate', label: '完成率(%)', type: 'percent', width: 120 },
    { key: 'cover', label: '覆盖人次', formatter: v => v.toLocaleString(), width: 100 }
  ]
  
  return exportToExcel(headers, data, filename, { sheetName: '普法任务报表' })
}

/**
 * 导出企业合规数据
 */
export async function exportComplianceReport(data, filename) {
  const headers = [
    { key: '_index', label: '序号' },
    { key: 'enterprise', label: '企业名称', width: 200 },
    { key: 'industry', label: '行业', width: 130 },
    { key: 'status', label: '合规状态', type: 'status', width: 110 },
    { key: 'lastCheck', label: '最近检查', width: 120 }
  ]
  
  return exportToExcel(headers, data, filename, { sheetName: '企业合规台账' })
}

/**
 * 导出线索举报数据
 */
export async function exportReportData(data, filename) {
  const headers = [
    { key: '_index', label: '序号' },
    { key: 'area', label: '区域', width: 160 },
    { key: 'total', label: '举报总数', width: 100 },
    { key: 'pending', label: '待处置', width: 100 },
    { key: 'processed', label: '已处置', width: 100 },
    { key: 'avgTime', label: '平均处置时长', width: 120 }
  ]
  
  return exportToExcel(headers, data, filename, { sheetName: '线索举报统计' })
}

/**
 * 导出全部报表（多工作表）
 */
export async function exportAllReports(taskData, complianceData, reportData, filename) {
  try {
    const wb = XLSX.utils.book_new()
    
    // 添加任务报表
    const taskHeaders = [
      { key: '_index', label: '序号' },
      { key: 'org', label: '组织' },
      { key: 'total', label: '任务总数' },
      { key: 'done', label: '已完成' },
      { key: 'rate', label: '完成率(%)' },
      { key: 'cover', label: '覆盖人次' }
    ]
    const taskWs = dataToWorksheet(taskHeaders, taskData)
    XLSX.utils.book_append_sheet(wb, taskWs, '普法任务报表')
    
    // 添加合规台账
    const complianceHeaders = [
      { key: '_index', label: '序号' },
      { key: 'enterprise', label: '企业名称' },
      { key: 'industry', label: '行业' },
      { key: 'status', label: '合规状态' },
      { key: 'lastCheck', label: '最近检查' }
    ]
    const complianceWs = dataToWorksheet(complianceHeaders, complianceData)
    XLSX.utils.book_append_sheet(wb, complianceWs, '企业合规台账')
    
    // 添加举报统计
    const reportHeaders = [
      { key: '_index', label: '序号' },
      { key: 'area', label: '区域' },
      { key: 'total', label: '举报总数' },
      { key: 'pending', label: '待处置' },
      { key: 'processed', label: '已处置' },
      { key: 'avgTime', label: '平均处置时长' }
    ]
    const reportWs = dataToWorksheet(reportHeaders, reportData)
    XLSX.utils.book_append_sheet(wb, reportWs, '线索举报统计')
    
    // 导出
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    downloadBlob(new Blob([wbout], { type: 'application/octet-stream' }), filename)
    
    return { success: true, filename }
  } catch (error) {
    console.error('Export all reports error:', error)
    return { success: false, error: error.message }
  }
}

export default {
  exportToExcel,
  exportToPDF,
  exportTaskReport,
  exportComplianceReport,
  exportReportData,
  exportAllReports,
  generateExportFilename,
  ExportColors
}
