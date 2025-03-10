
data class Setting(
    val enable : Boolean= false,
    val loginId : String = "",
    val password: String="",
    val from : String="",
    val to:String="",
    val bcc: String="",
    val subject: String="Audit Finished",
    val body: String="We would like to notify you that the vulnerability scan has been completed successfully.\n" +
            "\n" +
            "Status: Completed\n" +
            "\n" +
            "Please review the issued report for detailed results.",
    val auditTouchExpires:Int = 10
)
