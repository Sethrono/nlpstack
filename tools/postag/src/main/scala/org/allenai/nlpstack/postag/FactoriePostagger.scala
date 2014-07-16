package org.allenai.nlpstack.postag

import org.allenai.nlpstack.core.postag.{ PostaggedToken, Postagger, PostaggerMain }
import org.allenai.nlpstack.core.tokenize.{ Token, Tokenizer }
import org.allenai.nlpstack.tokenize.defaultTokenizer

import cc.factorie.app.nlp._
import cc.factorie.app.nlp.pos.OntonotesForwardPosTagger

/** This is thread-safe. The only thing we call on OntonotesForwardPosTagger is
  * predict(), and predict() is threadsafe. I don't know about the other methods
  * on OntonotesForwardPosTagger.
  */
class FactoriePostagger extends Postagger {
  private val tagger = OntonotesForwardPosTagger

  override def postagTokenized(tokens: Seq[Token]): Seq[PostaggedToken] = {
    // translate the tokens into a Factorie document
    val factorieDoc = new Document(Tokenizer.originalText(tokens))
    val factorieTokens = tokens.map(
      t => new cc.factorie.app.nlp.Token(factorieDoc, t.offset, t.offset + t.string.length))

    tagger.predict(factorieTokens) // modifies factoryTokens

    for (token <- factorieTokens) yield {
      PostaggedToken(tagger.tokenAnnotationString(token), token.string, token.stringStart)
    }
  }
}

object FactoriePostaggerMain extends PostaggerMain {
  override val tokenizer = defaultTokenizer
  override val postagger = new FactoriePostagger()
}
