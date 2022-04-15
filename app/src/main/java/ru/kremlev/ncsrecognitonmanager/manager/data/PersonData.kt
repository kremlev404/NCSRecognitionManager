package ru.kremlev.ncsrecognitonmanager.manager.data

data class PersonData(
    var personID: String = "Not chosen",
    var probs: ArrayList<Float>,
    var timestamps: ArrayList<TimeStamp>
)
