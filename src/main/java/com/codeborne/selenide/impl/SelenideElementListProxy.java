package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class SelenideElementListProxy implements InvocationHandler {

  @SuppressWarnings("unchecked")
  public static List<SelenideElement> wrap(Driver driver, ElementLocator locator) {
    InvocationHandler handler = new SelenideElementListProxy(driver, locator);

    return (List<SelenideElement>) Proxy.newProxyInstance(
      SelenideElementListProxy.class.getClassLoader(), new Class[]{List.class}, handler);
  }

  private final Driver driver;
  private final ElementLocator locator;

  private SelenideElementListProxy(Driver driver, ElementLocator locator) {
    this.driver = driver;
    this.locator = locator;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    List<SelenideElement> elements = new ArrayList<>();
    try {
      for (WebElement webElement : locator.findElements()) {
        elements.add(WebElementWrapper.wrap(driver, webElement));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      return method.invoke(elements, args);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }
}
