import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class Sender {
    fun send(test: Boolean) {
        val from = settingHolder.setting.from
        val password = settingHolder.setting.password
        val to = settingHolder.setting.to
        val bcc = settingHolder.setting.bcc
        var title = "Audit Finished!!"
        var body = "We would like to notify you that the vulnerability scan has been completed successfully.\n" +
                "\n" +
                "Status: Completed\n" +
                "\n" +
                "Please review the issued report for detailed results."
        if (test){
            title = "test send mail"
            body = "This is a test email.  \n" +
                    "It was sent from the Burp Suite extension \"AuditNotify.\""
        }

        sendMail(from, password, to, bcc, title, body)
    }

    private fun sendMail(from: String, password: String, to: String, bcc: String, title: String, body: String) {
        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(from, password)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(from))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc))
                subject = title
                setText(body)
            }

            Transport.send(message)
            println("Email sent successfully to $to")
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}