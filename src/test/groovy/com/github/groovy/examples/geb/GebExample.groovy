package com.github.groovy.examples.geb

import geb.Browser

Browser.drive {
    go "http://gebish.org"

    assert title == "Geb - Very Groovy Browser Automation" 

    $("#sidebar .sidemenu a", text: "jQuery-like API").click()

    //assert $("#main h1")*.text() == ["Navigating Content", "Form Control Shortcuts"] 
    //assert $("#sidebar .sidemenu a", text: "jQuery-like API").parent().hasClass("selected")
}