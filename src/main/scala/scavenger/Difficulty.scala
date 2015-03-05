package scavenger

/**
 * This enumeration describes three types of difficulty 
 * of the jobs. If a job is trivial, it can be executed
 * on either master and worker nodes.
 * If job is marked as parallelizable, it should be taken
 * care by the master.
 * If job is marked as `ComputeHeavy`, it should be 
 * evaluated on a worker node.
 */
sealed trait Difficulty
case object ComputeHeavy extends Difficulty
case object Parallelizable extends Difficulty
case object Trivial extends Difficulty