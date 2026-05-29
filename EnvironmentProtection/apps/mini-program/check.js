const fs = require('fs')
const c = fs.readFileSync('dist/dev/mp-weixin/pages/login/login.js', 'utf8')

// Find and show _sfc_main structure
const sfcIdx = c.indexOf('_sfc_main = {')
const renderIdx = c.indexOf('_sfc_render')
console.log('=== _sfc_main (lines', sfcIdx, '-', renderIdx, ') ===')
console.log(c.slice(sfcIdx, Math.min(sfcIdx + 3000, renderIdx)))
