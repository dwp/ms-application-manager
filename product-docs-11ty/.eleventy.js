const govukEleventyPlugin = require('govuk-eleventy-plugin')
const eleventyNavigationPlugin = require("@11ty/eleventy-navigation")

module.exports = function(eleventyConfig) {

  const { SERVICE_NAME, BRAND_COLOUR, API_DIRECTORY, ADDITIONAL_DISTRIBUTION } = process.env;
  const { CI_PAGES_URL, CI_COMMIT_REF_NAME, CI_PROJECT_DIR } = process.env;
  
  let PATH_PREFIX = '';
  let BASE_URL = '';

  if (CI_PAGES_URL) {
    const PAGES_PATH_REGEX = /https:\/\/[^\/]+(.+)/g;
    const PAGES_PATH_REGEX_MATCHES = CI_PAGES_URL.matchAll(PAGES_PATH_REGEX);
    PATH_PREFIX = [...PAGES_PATH_REGEX_MATCHES][0][1];
    BASE_URL = CI_PAGES_URL;
  } 

  let BANNER = '';
  if (CI_COMMIT_REF_NAME && CI_COMMIT_REF_NAME != "develop") {
    BANNER = ` (Preview of ${CI_COMMIT_REF_NAME})`
  }

  const eleventyOptions = {
    header: {
      search: {
        label: 'Search documentation',
        indexPath: `${BASE_URL}/search.json`,
        sitemapPath: `${BASE_URL}/sitemap.html`
      },
      organisationName: 'DWP',
      productName: (SERVICE_NAME ?? 'Provide SERVICE_NAME variable') + BANNER,
      homepageUrl: BASE_URL.length !== 0 ? BASE_URL : '/'
    },
    icons: {
      mask: `${BASE_URL}/assets/images/govuk-mask-icon.svg`,
      shortcut: `${BASE_URL}/assets/images/favicon.ico`,
      touch: `${BASE_URL}/assets/images/govuk-apple-touch-icon.png`
    },
    opengraphImageUrl: `${BASE_URL}/assets/images/govuk-opengraph-image.png`,
    brandColour: BRAND_COLOUR,
    headingPermalinks: true,
    url: BASE_URL,
    pathPrefix: PATH_PREFIX,
    stylesheets: [
      `${BASE_URL}/assets/govuk-custom.css`
    ],
    govukAssetsPath: `${BASE_URL}/assets/`
  };

  // Register the plugin
  eleventyConfig.addPlugin(govukEleventyPlugin, eleventyOptions)
  eleventyConfig.addPlugin(eleventyNavigationPlugin)
  eleventyConfig.addPassthroughCopy("images");
  eleventyConfig.addPassthroughCopy("custom-css");  //if you have any custom css, ensure files are added to options->stylesheets array

  // The govuk eleventy plugin does not currently cater for js at a sub directory path, update the location as a post process
  // Also the govuk eleventy plugin does not provide govuk-asset-path during scss compilation, so remove the default entry and compile and add the css as a post event
  eleventyConfig.addTransform("govuk_css_js", function(content, outputPath) {
    if( outputPath && outputPath.endsWith(".html") ) {
      let updatedContent = content;
      updatedContent =  updatedContent.replaceAll('<script src="/assets/govuk.js"></script>', `<script src="${BASE_URL}/assets/govuk.js"></script>`);
      updatedContent =  updatedContent.replaceAll(/<.*govuk\.css.*>/g, '');
      return updatedContent;
    }
    return content;
  });

  // If we are dealing with open-api specs, copy the resources into the bundle, and update all links
  if (CI_PROJECT_DIR && API_DIRECTORY) {
    const openApiDir = `${CI_PROJECT_DIR}/openapi-spec`;
    eleventyConfig.addPassthroughCopy( {openApiDir: 'openapi-spec'} );

    eleventyConfig.addTransform("openapi_html", function(content, outputPath) {
      if( outputPath && outputPath.endsWith(".html") ) {
        let updatedContent = content;
        updatedContent =  updatedContent.replaceAll('~#DWP-OPENAPI#~', `${BASE_URL}/openapi-spec/html.snippet`);
        return updatedContent;
      }
      return content;
    });
  }

  // If there are any additional distrubutions specified, copy these into the bundle
  if (ADDITIONAL_DISTRIBUTION) {
    const distributions = ADDITIONAL_DISTRIBUTION.split(',');
    for (let distributionIndex in distributions) {
      eleventyConfig.addPassthroughCopy(distributions[distributionIndex]);
    }
  }

  // The govuk eleventy plugin does not currently cater for sub-directory paths in search.json, update the locations as a post process
  eleventyConfig.addTransform("search-paths", function(content, outputPath) {
    if( outputPath && outputPath.endsWith("search.json") ) {
      return content.replaceAll('"url": "/', `"url": "${BASE_URL}/`);
    }
    return content;
  });

  // Events - process govuk scss and take into account assets path (not currently done by plugin - see https://github.com/x-govuk/govuk-eleventy-plugin/issues/60)
  eleventyConfig.on('eleventy.after', async () => {
    require('./lib/eleventy-generate-govuk-assets.js')(eleventyConfig, eleventyOptions)
  })

  return {
    dataTemplateEngine: 'njk',
    htmlTemplateEngine: 'njk',
    markdownTemplateEngine: 'njk',
    dir: {
      // Use layouts from the plugin
      layouts: 'node_modules/govuk-eleventy-plugin/layouts'
    }
  }
};