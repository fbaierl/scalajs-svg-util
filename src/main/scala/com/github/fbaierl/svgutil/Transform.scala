package com.github.fbaierl.svgutil

import scala.collection.mutable

object Transform {

  def apply(): Transform = Transform()

  def fromString(raw: String): Transform = {
    var translateX, translateY, rot, rotOriginX, rotOriginY, skewX, skewY, scaleX, scaleY = 0d
    findTranformFunctions(raw).foreach {
      func =>
        val parameters = findTransformFunctionParameters(func._2)
        if(parameters.nonEmpty){
          func._1.toLowerCase match {
            case "translate" =>
              translateX = parameters.head
              if(parameters.size > 1) translateY = parameters(1)
            case "scale" =>
              scaleX = parameters.head
              if(parameters.size > 1) scaleY = parameters(1)
            case "rotate" =>
              rot = parameters.head
              if(parameters.size > 1) rotOriginX = parameters(1)
              if(parameters.size > 2) rotOriginY = parameters(2)
            case "skewX" =>
              skewX = parameters.head
            case "skewY" =>
              skewY = parameters.head
          }
        }
    }
    Transform(translateX, translateY, rot, rotOriginX, rotOriginY, skewX, skewY, scaleX, scaleY)
  }

  private def findTransformFunctionParameters(s: String): Seq[Double] = {
    var result = Seq[Double]()
    """([-]?\d+[.]?[\d]*)""".r.findAllIn(s).matchData.foreach {
      singleMatch =>
        result = result :+ singleMatch.toString.toDouble
    }
    result
  }

  /**
    *
    * @param s
    * @return
    */
  private def findTranformFunctions(s: String): Set[(String, String)] = {
    var result = scala.collection.mutable.Set[(String, String)]()
    """((\w+)\((.*?)\))?""".r.findAllIn(s).matchData.foreach {
      singleMatch =>
      if(!singleMatch.toString().isEmpty && singleMatch.subgroups.size >= 3){
        val ele = singleMatch.subgroups(1)
        val modifiers = singleMatch.subgroups(2)
        result += ((ele, modifiers))
      }
    }
    Set.empty ++ result
  }

  private def asdf(s: String): (String, String) = {



    /*
    if(matches.size <= 0){
      // no translation value found; return (0, 0)
      (0d, 0d)
    } else {
      val values = matches.head.split(",")
        .toList
        .map(safeToDouble)
        .map(_.getOrElse(return (0,0)))

      if(values.isEmpty) {
        // something went wrong
        (0, 0)
      } else if (values.size == 1){
        // only tx set
        (values.head, 0)
      } else {
        (values.head, values(1))
      }

     */
    ("","")
  }

}

case class Transform(translateX: Double,
                     translateY:Double,
                     rot: Double,
                     rotOriginX: Double,
                     rotOriginY: Double,
                     skewX: Double,
                     skewY: Double,
                     scaleX: Double,
                     scaleY: Double) {


  // https://stackoverflow.com/questions/17824145/parse-svg-transform-attribute-with-javascript

  def rotate(rot: Double, rotOriginX: Double = 0, rotOriginY: Double = 0): Transform =
    Transform(translateX, translateY, this.rot + rot, this.rotOriginX + rotOriginX, this.rotOriginY + rotOriginY,
      skewX, skewY, scaleX, scaleY)

  /**
    * Moves the object by x and y.
    */
  def translate(x: Double, y: Double = 0): Transform =
    Transform(translateX + x, translateY + y, rot, rotOriginX, rotOriginY, skewX, skewY, scaleX, scaleY)

  def skew(): Unit = {

  }

  override def toString: String =
      s"rotate($rot $rotOriginX $rotOriginY)" +
      s"translate($translateX $translateY)" +
      s"skewX($skewX)" +
      s"skewY($skewY)" +
      s"scale($scaleX $scaleY)"

}


/*

object SVGUtil {
  /**
    * Adds the specified rotation (in deg) to the given transform string.
    * Returns the transformation string with the rotatioin in degrees.
    * Rotation origin remains unchanged.
    */
  def addRotationDegToTransformString(transform: String, deg: Double): String = {
    if(transform == null || transform.isEmpty){
      s"rotate($deg)"
    } else {
      def rotation: (Double, Double, Double) = getRotationInDegFromTransformString(transform)
      val rotateR = raw"""rotate\(.*?\)""".r
      val matches = rotateR.findAllIn(transform).toList
      var newTransform = transform

      // remove all rotations from the old string
      matches.foreach(m => newTransform = newTransform.replace(m, ""))

      // remove exponential notation (e)
      val rot = f"${rotation._1 + deg}%.1f"
      val rotOriginX = f"${rotation._2}%.1f"
      val rotOriginY = f"${rotation._3}%.1f"

      // transform may have a trailing "," after the last ")"
      newTransform = newTransform.trim().replace("),", ")").trim()

      s"rotate($rot,$rotOriginX,$rotOriginY) $newTransform"
    }
  }

  private def safeToDouble(s: String) : Option[Double] = {
    try {
      Some(s.toDouble)
    } catch {
      case _: NumberFormatException => None
    }
  }

  /**
    * Returns the rotation of a transform string in degrees.
    * Returns (0,0,0) if:
    *   - no rotation value can be found
    *   - string is malformed
    *   - string is null
    * If more than three (rot, rotOriginX and rotOriginY) rotation values are found (not defined in css),
    * only the first three values are returned.
    */
  def getRotationInDegFromTransformString(transform: String) : (Double, Double, Double) = {

    if(transform == null){
      return (0,0,0)
    }

    def gradToDeg(grad: Double) = grad * 0.9
    def radToDeg(rad: Double) = Math.toDegrees(rad)
    def turnToDeg(turn: Double) = turn * 360
    val rotateRegex = raw"""(?<=rotate\().*?(?=\))""".r
    val matches = rotateRegex.findAllIn(transform).toList

    if(matches.size <= 0){
      return (0,0,0)
    }

    val values = matches.head.split(",").toList

    // rotation
    val rotationValueR = raw"""([-+]?[0-9]*\.?[0-9]+)(.*)""".r
    var newRotation: Double = 0

    values.head match {
      case rotationValueR(digit, angleType) =>
        if(digit.isEmpty) {
          // no rotation
          newRotation = 0
        } else if(angleType.isEmpty){
          // rotation w/o "deg"/"rad"/etc.
          newRotation = safeToDouble(digit).getOrElse(0d)
        } else {
          // rotation with "deg"/"rad"/etc.
          val rotation = safeToDouble(digit).getOrElse(0d)
          newRotation = angleType match {
            case "rad"  => radToDeg(rotation)
            case "grad" => gradToDeg(rotation)
            case "turn" => turnToDeg(rotation)
            case "deg"  => rotation
            case _      => 0
          }
        }
      case _ => newRotation = 0
    }

    // rotation origin
    val rotationOriginValues = values.drop(1) // w/o rotate
      .map(safeToDouble)
      .map(_.getOrElse(return (newRotation, 0, 0)))
    if (rotationOriginValues.isEmpty) {
      // something went wrong
      (newRotation, 0, 0)
    } else if (values.size == 1) {
      // only x set
      (newRotation, rotationOriginValues.head, 0)
    } else {
      (newRotation, rotationOriginValues.head, rotationOriginValues(1))
    }
  }

  /**
    * Returns the translation of a transform string.
    * Returns (0,0) if:
    *   - no translation value can be found
    *   - string is malformed
    *   - string is null
    * If more than two (tx and ty) transformation values are found (not defined in css),
    * only the first two values are returned.
    */
  def getTranslationFromTransformString(transform: String) : (Double, Double) = {

    if(transform == null){
      return (0,0)
    }

    val transformR = raw"""(?<=translate\().*?(?=\))""".r
    val matches = transformR.findAllIn(transform).toList

    if(matches.size <= 0){
      // no translation value found; return (0, 0)
      (0d, 0d)
    } else {
      val values = matches.head.split(",")
        .toList
        .map(safeToDouble)
        .map(_.getOrElse(return (0,0)))

      if(values.isEmpty) {
        // something went wrong
        (0, 0)
      } else if (values.size == 1){
        // only tx set
        (values.head, 0)
      } else {
        (values.head, values(1))
      }
    }
  }
}
 */