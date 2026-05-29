$ErrorActionPreference = 'Stop'
$dir = Join-Path $PSScriptRoot '..\static\tabbar'
New-Item -ItemType Directory -Force -Path $dir | Out-Null
Add-Type -AssemblyName System.Drawing

function New-TabIcon {
  param(
    [string]$Path,
    [int]$BgR, [int]$BgG, [int]$BgB,
    [int]$FgR, [int]$FgG, [int]$FgB,
    [string]$Letter
  )
  $bmp = New-Object System.Drawing.Bitmap 81, 81
  $g = [System.Drawing.Graphics]::FromImage($bmp)
  $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
  $bg = [System.Drawing.Color]::FromArgb($BgR, $BgG, $BgB)
  $g.Clear($bg)
  $font = New-Object System.Drawing.Font 'Segoe UI', 36, ([System.Drawing.FontStyle]::Bold), ([System.Drawing.GraphicsUnit]::Pixel)
  $brush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb($FgR, $FgG, $FgB))
  $sf = New-Object System.Drawing.StringFormat
  $sf.Alignment = [System.Drawing.StringAlignment]::Center
  $sf.LineAlignment = [System.Drawing.StringAlignment]::Center
  $rect = New-Object System.Drawing.RectangleF 0, 0, 81, 81
  $g.DrawString($Letter, $font, $brush, $rect, $sf)
  $bmp.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
  $g.Dispose()
  $bmp.Dispose()
}

$pairs = @(
  @('home.png', 245, 245, 245, 120, 120, 120, 'H'),
  @('home-active.png', 40, 167, 69, 255, 255, 255, 'H'),
  @('learn.png', 245, 245, 245, 120, 120, 120, 'L'),
  @('learn-active.png', 40, 167, 69, 255, 255, 255, 'L'),
  @('task.png', 245, 245, 245, 120, 120, 120, 'T'),
  @('task-active.png', 40, 167, 69, 255, 255, 255, 'T'),
  @('chat.png', 245, 245, 245, 120, 120, 120, 'C'),
  @('chat-active.png', 40, 167, 69, 255, 255, 255, 'C'),
  @('profile.png', 245, 245, 245, 120, 120, 120, 'M'),
  @('profile-active.png', 40, 167, 69, 255, 255, 255, 'M')
)

foreach ($p in $pairs) {
  $out = Join-Path $dir $p[0]
  New-TabIcon -Path $out -BgR $p[1] -BgG $p[2] -BgB $p[3] -FgR $p[4] -FgG $p[5] -FgB $p[6] -Letter $p[7]
}

Write-Host "OK $dir"
