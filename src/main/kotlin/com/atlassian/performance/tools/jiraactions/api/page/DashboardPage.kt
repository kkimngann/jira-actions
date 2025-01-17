package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.*

class DashboardPage(
    private val driver: WebDriver
) {
    private val jiraErrors = JiraErrors(driver)
    private val popUps = NotificationPopUps(driver)

    fun dismissAllPopups() {
        popUps
            .disableNpsFeedback()
            .dismissJiraHelpTips()
            .dismissPostSetup()
            .waitUntilAuiFlagsAreGone()
    }

    fun waitForDashboard(): DashboardPage {
        driver.wait(
            or(
                and(
                    presenceOfElementLocated(By.className("page-type-dashboard")),
                    CheckIFrame()
                ),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        return this
    }

    internal fun getPopUps(): NotificationPopUps {
        return popUps
    }

    private class CheckIFrame : ExpectedCondition<Boolean> {
        override fun apply(input: WebDriver?): Boolean? {
            input as JavascriptExecutor
            //we currently support only single iframe on dashboard in this check
            return input.executeScript(
                """
                if (typeof $ === 'undefined') { //wait for jquery
                    return false;
                }
                iframes = $('#dashboard').find('iframe');
                return iframes.length === 1 && iframes.contents().find('body').children().length > 0
                """
            ) as Boolean
        }
    }
}
