const fs = require("fs").promises

const isPassed = (result) => {
  return result.includes("No issues found") || result.includes("0 errors, 0 warnings")
}

module.exports = async ({ core, resultFile }) => {
  const result = await fs.readFile(resultFile, 'utf-8')

  core.debug(`result=${result}`)

  if (!isPassed(result)) {
    await core.summary
      .addHeading("🔥 Android Lint", 2)
      .addCodeBlock(result)
      .write()

    core.setFailed("The linting failed. Please check the job summary.")
  }
}
