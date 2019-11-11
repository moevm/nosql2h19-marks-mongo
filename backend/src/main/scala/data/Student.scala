package data

case class Student(id: String,
                   name: String,
                   surname: String,
                   sex: Boolean,
                   groups: Seq[Int],
                   marks: Seq[Mark] = Seq.empty)
