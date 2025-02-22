package application.browser;

import com.aa.mtg.cards.Card;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class CardHelper {
    private WebElement webElement;
    private MtgBrowser mtgBrowser;

    CardHelper(WebElement webElement, MtgBrowser mtgBrowser) {
        this.webElement = webElement;
        this.mtgBrowser = mtgBrowser;
    }

    public static List<String> cardNames(Card... cards) {
        return Stream.of(cards).map(Card::getName).sorted().collect(toList());
    }

    public static List<String> cardNames(WebElement element) {
        List<WebElement> cardElements = element.findElements(By.className("card"));
        return cardElements.stream()
                .map(cardElement -> cardElement.getAttribute("aria-label"))
                .sorted()
                .collect(toList());
    }

    public void click() {
        try {
            webElement.click();
        } catch (ElementClickInterceptedException e) {
            webElement.findElement(By.tagName("div")).click();
        }
    }

    public void tap() {
        canCurrentUserContinue();
        click();
        isFrontendTapped();
    }

    public void select() {
        canCurrentUserContinue();
        click();
        isSelected();
    }

    public void declareAsAttacker() {
        declareAsAttackerOrBlocker();
    }

    public void declareAsBlocker() {
        declareAsAttackerOrBlocker();
    }

    public String getCardId() {
        return webElement.getAttribute("id");
    }

    public int getCardIdNumeric() {
        return Integer.parseInt(getCardId().split("-")[1]);
    }

    public void isFrontendTapped() {
        hasClass("frontend-tapped");
    }

    public void isNotFrontendTapped() {
        doesNotHaveClass("frontend-tapped");
    }

    public void isTapped() {
        hasClass("tapped");
        doesNotHaveClass("frontend-tapped");
    }

    public void isTappedDoesNotUntapNextTurn() {
        hasClass("tapped-does-not-untap-next-turn");
    }

    public void isNotTapped() {
        doesNotHaveClass("tapped");
    }

    public void isTargeted() {
        hasClass("targeted");
    }

    public void isSelected() {
        hasClass("selected");
    }

    public void isNotSelected() {
        doesNotHaveClass("selected");
    }

    public void hasSummoningSickness() {
        mtgBrowser.wait(presenceOfElementLocated(cardCssSelector(".summoning-sickness")));
    }

    public void doesNotHaveSummoningSickness() {
        waitForAbsenceOfElement(cardCssSelector(".summoning-sickness"));
    }

    public void hasFlying() {
        hasClass("flying");
    }

    public void hasDamage(int damage) {
        mtgBrowser.wait(textToBe(cardCssSelector(".damage"), String.valueOf(damage)));
    }

    public void doesNotHaveDamage() {
        waitForAbsenceOfElement(cardCssSelector(".damage"));
    }

    public void hasPowerAndToughness(String powerAndToughness) {
        mtgBrowser.wait(textToBe(cardCssSelector(".power-toughness"), String.valueOf(powerAndToughness)));
    }

    public void parentHasStyle(String style) {
        WebElement parent = webElement.findElement(By.xpath("./.."));
        assertThat(parent.getAttribute("style")).contains(style);
    }

    private void hasClass(String classValue) {
        mtgBrowser.wait(attributeContains(By.id(getCardId()), "class", classValue));
    }

    private void doesNotHaveClass(String classValue) {
        mtgBrowser.wait(not(attributeContains(By.id(getCardId()), "class", classValue)));
    }

    private By cardCssSelector(String cssSelector) {
        return By.cssSelector("#" + getCardId() + " " + cssSelector);
    }

    private void waitForAbsenceOfElement(By locator) {
        try {
            mtgBrowser.findElement(locator);
            mtgBrowser.wait(invisibilityOfElementLocated(locator));

        } catch (NotFoundException e) {
            System.out.println("Element " + locator + " is not present at all. That's okay.");
        }
    }

    private void canCurrentUserContinue() {
        new ActionHelper(mtgBrowser).canContinue();
    }

    private void declareAsAttackerOrBlocker() {
        canCurrentUserContinue();
        String cardId = this.getCardId();
        click();
        mtgBrowser.wait(presenceOfElementLocated(By.cssSelector(".combat-line #" + cardId)));
    }

    public void hasPlus1Counters(int counters) {
        mtgBrowser.wait(textToBe(cardCssSelector(".plus-1-counters"), String.valueOf(counters)));
    }
}
