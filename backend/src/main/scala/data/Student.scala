package data

case class Student(id: String,
                   name: String,
                   surname: String,
                   sex: String,
                   groups: Seq[Int],
                   marks: Seq[Mark] = Seq.empty)
