package com.github.fbaierl.svgutil

import org.scalatest.FlatSpec

class TransformSpec extends FlatSpec {

  "Transform" should "parse a string without commas or whitespaces" in {


      Transform.fromString("rotate(-10.3 50 100.22)translate(-36,45.5)skewX(40)scale(1,0.5,12.2)")

    assert(false)

    }

    it should "parse a string with commas" in {

//      Transform.fromString("rotate(-10 50 100),translate(-36 45.5),skewX(40),scale(1 0.5)")

    }


    it should "parse a string with whitespaces" in {

//      Transform.fromString("rotate(-10 50 100) translate(-36 45.5) skewX(40) scale(1 0.5)")

    }


}
