package com.atlassian.performance.tools.jiraactions.api.page

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions.*

class HistoryTabPanel(
    private val driver: WebDriver
) {
    // the duration is set to such value because viewing issue with many change history entries is a heavy operation,
    // and we assume the user is patient enough to wait for all entries to show up
    private val loadMoreEntriesLocator = By.cssSelector("button.show-more-changehistory-tabpanel")
    private val attemptLimit = 5

    fun showAllHistoryEntries(): HistoryTabPanel {
        if (driver.isElementPresent(loadMoreEntriesLocator)) {
            return showMoreEntriesIfNeeded()
        }
        driver.wait(invisibilityOfElementLocated(By.cssSelector(".issuePanelWrapper.loading")))
        return this
    }

    fun waitForActive(): HistoryTabPanel {
        driver.wait(elementToBeClickable(By.id("changehistory-tabpanel"))).click()
        driver.wait(presenceOfElementLocated(By.cssSelector("#changehistory-tabpanel.active")))
        return this
    }

    private fun showMoreEntriesIfNeeded(): HistoryTabPanel {
        repeat(attemptLimit) {
            if (!driver.isElementPresent(loadMoreEntriesLocator)) {
                return this
            }
            val loadMoreButton = driver.findElement(loadMoreEntriesLocator)
            Actions(driver)
                .keyDown(Keys.SHIFT)
                .click(loadMoreButton)
                .keyUp(Keys.SHIFT)
                .build()
                .perform()
            driver.wait(invisibilityOfElementLocated(By.xpath("//button[@aria-busy='true']")))
        }
        return this
    }
}
