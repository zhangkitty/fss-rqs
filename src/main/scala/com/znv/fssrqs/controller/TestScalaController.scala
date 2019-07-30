package com.znv.fssrqs.controller

import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}


@RestController
class TestScalaController {

  @RequestMapping(value = Array("/sayScalaHello"),method = Array(RequestMethod.GET))
  def sayScalaHello() = {

    "Hello Scala Boot...."
  }
}
