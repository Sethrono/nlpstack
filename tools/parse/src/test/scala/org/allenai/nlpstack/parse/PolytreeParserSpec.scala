package org.allenai.nlpstack.parse

import org.allenai.common.testkit.UnitSpec
import org.allenai.nlpstack.core.parse.graph.DependencyGraph
import org.allenai.nlpstack.postag.defaultPostagger
import org.allenai.nlpstack.tokenize.defaultTokenizer

class PolytreeParserSpec extends UnitSpec {
  private def parseTreeString(text: String) = {
    val tokens = defaultTokenizer.tokenize(text)
    val postaggedTokens = defaultPostagger.postagTokenized(tokens)

    val parser = new PolytreeParser
    val parseTree = parser.dependencyGraphPostagged(postaggedTokens)

    DependencyGraph.multilineStringFormat.write(parseTree)
  }

  /*
   * When these tests fail with anything but an exception, it's a judgement call
   * whether the trees that the parser produces are valid parses or whether this
   * is a genuine error. If in doubt, consult your favorite linguist, but by and
   * large, don't worry too much about accuracy here. This is not a quality test
   * suite.
   */

  "PolytreeParserParser" should "correctly parse a simple sentence" in {
    val parseTreeStr = parseTreeString("A waffle is like a pancake with a syrup trap.")
    val expectedParseTreeStr =
      """|det(waffle-2, A-1)
         |nsubj(is-3, waffle-2)
         |root(ROOT-0, is-3)
         |adpmod(is-3, like-4)
         |det(pancake-6, a-5)
         |adpobj(like-4, pancake-6)
         |adpmod(is-3, with-7)
         |det(trap-10, a-8)
         |compmod(trap-10, syrup-9)
         |adpobj(with-7, trap-10)
         |p(is-3, .-11)""".stripMargin
    assert(parseTreeStr === expectedParseTreeStr)
  }

  it should "correctly parse a complicated sentence" in {
    // This sentence has two roots when it comes out of Factorie, so we want to
    // test the same case here.
    val parseTreeStr = parseTreeString("Big investment banks refused to step up to the plate, traders say.")
    val expectedParseTreeStr =
      """|amod(banks-3, Big-1)
         |compmod(banks-3, investment-2)
         |nsubj(refused-4, banks-3)
         |root(ROOT-0, refused-4)
         |aux(step-6, to-5)
         |xcomp(refused-4, step-6)
         |prt(step-6, up-7)
         |adpmod(step-6, to-8)
         |det(plate-10, the-9)
         |adpobj(to-8, plate-10)
         |p(say-13, ,-11)
         |nsubj(say-13, traders-12)
         |ccomp(refused-4, say-13)
         |p(refused-4, .-14)""".stripMargin
    assert(parseTreeStr === expectedParseTreeStr)
  }
}
