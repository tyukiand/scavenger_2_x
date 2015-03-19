package scavenger.categories.freeccc

/** Implementation of a free cartesian closed
  * category generated by a graph.
  *
  * The nodes of the graph are Scala-types.
  * The edges of the graph are simple Strings.
  *
  * @since 2.1
  * @author Andrey Tyukin
  */
trait Arrow[-X, +Y] {
  def o[W](other: Arrow[W, X]): Arrow[W, Y]

  /** This method is needed in order to generate
    * readable `toString` output, without generating
    * unnecessary parens.
    */
  protected def needsParens: Boolean = false
  def toParenString = 
    if (needsParens) "(%s)".format(this.toString)
    else this.toString
}

/** Edges of the graph are the simplest terms that are
  * included in the Free Cartesian Closed Category.
  */
case class Edge[-X, +Y](label: String) extends Arrow[X, Y] {
  override def toString = label
  def o[W](other: Arrow[W, X]) = other match {
    case i: Identity[_] => this.asInstanceOf[Arrow[W, Y]]
    case _ => Composition(this, other)
  }
}

/** Identity morphism at node `X`.
  */
case class Identity[X]() extends Arrow[X, X] {
  override def toString = "Id"
  def o[W](other: Arrow[W, X]) = other
}

/** Formal composition of two arrows.
  */
case class Composition[-X, Y, +Z](
  second: Arrow[Y, Z],
  first: Arrow[X, Y]
) extends Arrow[X, Z] {

  def o[W](other: Arrow[W, X]): Arrow[W, Z] = other match {
    case i: Identity[_] => this.asInstanceOf[Arrow[W, Z]]
    case _ => second o (first o other)
  }

  // TODO: set it to `false` after verifying strong normalization
  override def needsParens = true
  override def toString = 
    second.toParenString + " o " + first.toParenString
}

/** Pair of two arrows with the same domain `X`.
  */
case class Pair[-X, +L, +R](left: Arrow[X, L], right: Arrow[X, R]) 
extends Arrow[X, (L, R)] {
  def o[W](other: Arrow[W, X]): Arrow[W, (L, R)] = other match {
    case i: Identity[_] => this.asInstanceOf[Arrow[W, (L, R)]]
    case _ => Pair(left o other, right o other)
  }
  override def toString = "<%s,%s>".format(left, right)
}

/** Projection to the first component.
  */
case class Fst[L, -R]() extends Arrow[(L, R), L] {
  def o[W](other: Arrow[W, (L, R)]): Arrow[W, L] = other match {
    case i: Identity[_] => this.asInstanceOf[Arrow[W, L]]
    case Pair(l, r) => l
  }
  override def toString = "Fst"
}

/** Projection to the second component.
  */
case class Snd[-L, R]() extends Arrow[(L, R), R] {
  def o[W](other: Arrow[W, (L, R)]): Arrow[W, R] = other match {
    case i: Identity[_] => this.asInstanceOf[Arrow[W, R]]
    case Pair(l, r) => r
  }
  override def toString = "Snd"
}

/** Abstraction (currying) transforms an arrow of type `(X, Y) => Z`
  * into an arrow of type `X => (Y => Z)`
  */
case class Curry[-X, -Y, +Z](f: Arrow[(X, Y), Z]) 
extends Arrow[X, Y => Z] {
  def o[W](other: Arrow[W, X]): Arrow[W, Y => Z] = other match {
    case i: Identity[_] => this.asInstanceOf[Arrow[W, Y => Z]]
    case _ => Composition(this, other)
  }
  override def toString = "lambda(%s)".format(f)
}

/** Evaluation can be thought of as a functional that takes a
  * tuple consisting of a function `f: Y => Z` and value `x: Y`
  * and returns `f(x)`.
  * In this formulation, both `f` and `x` are morphisms with
  * the same domain.
  */
case class Eval[Y, Z]()
extends Arrow[(Y => Z, Y), Z] {
  def o[W](other: Arrow[W, (Y => Z, Y)]): Arrow[W, Z] = other match {
    case i: Identity[_] => this.asInstanceOf[Arrow[W, Z]]
    case Pair(Composition(Curry(f), x), y) => f o Pair(x, y)
    case f => Composition(this, f)
  }
  override def toString = "eval"
}

/* little test, runnable as separate script
val x = Edge[Unit, Int]("x")
val y = Edge[Unit, Double]("y")
val e = 
  Eval[Double, Int] o 
  Pair(Curry(Fst[Int, Double]) o x, y)

println(e)
*/
