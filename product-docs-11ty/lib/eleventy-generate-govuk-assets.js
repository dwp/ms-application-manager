const path = require('node:path')
const fs = require('node:fs/promises')
const sass = require('sass')
const rollup = require('rollup')
const commonJs = require('@rollup/plugin-commonjs')
const { nodeResolve } = require('@rollup/plugin-node-resolve')

/**
 * Generate GOV.UK Frontend assets
 *
 * @param {object} eleventyConfig - Eleventy config
 * @param {object} options - Plugin options
 * @returns {function}
 */
module.exports = async function (eleventyConfig, options) {
  // eleventyConfig does not provide the default value for dir.output
  // https://github.com/11ty/eleventy/blob/36713b3af81b08530fac532ceef24f5dde8acb36/src/defaultConfig.js#L64
  const outputDir = eleventyConfig.dir.output || '_site'

  const govukBrandColour = options.brandColour ? options.brandColour : ''
  const govukFontFamily = options.fontFamily ? options.fontFamily : ''
  const govukAssetsPath = options.govukAssetsPath ? options.govukAssetsPath : '/assets/'

  // Get plugin options and set GOV.UK Frontend variables if provided
  const inputFilePath = path.join(__dirname, '../node_modules/govuk-eleventy-plugin/lib/govuk.scss')
  const inputFile = await fs.readFile(inputFilePath)
  const source = [
    govukBrandColour ? `$govuk-brand-colour: ${govukBrandColour};` : [],
    govukFontFamily ? `$govuk-font-family: ${govukFontFamily};` : [],
    govukAssetsPath ? `$govuk-assets-path: '${govukAssetsPath}';` : [],
    inputFile
  ].join('\n')

  // Generate CSS
  try {
    const outputFile = `${outputDir}/assets/govuk-custom.css`
    const result = sass.compileString(source, {
      loadPaths: [
        path.join(__dirname, '..'),
        './node_modules',
        './node_modules/govuk-eleventy-plugin'
      ],
      quietDeps: true
    })
    fs.writeFile(outputFile, result.css)
  } catch (error) {
    console.error(error)
  }
}
