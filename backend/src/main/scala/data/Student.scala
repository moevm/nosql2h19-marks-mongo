package data

case class Student(id: String,
                   name: String,
                   surname: String,
                   sex: Boolean,
                   group: Int,
                   marks: Seq[Mark] = Seq.empty)
