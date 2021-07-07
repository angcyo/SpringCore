package com.angcyo.java.mail

import com.angcyo.spring.base.beanOf
import com.angcyo.spring.util.L
import org.simplejavamail.api.email.Email
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

/**
 * 发错错误码参考:
 * http://help.163.com/09/1224/17/5RAJ4LMH00753VB8.html
 *
 * https://www.simplejavamail.org/configuration.html
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/01/20
 */

class DslMail {

    var debug: Boolean = L.isDebug

    /**发送邮件服务器的地址*/
    var smtpHost: String = "smtp.126.com"

    /**发送邮件服务器的端口*/
    var smtpPort: Int = 25

    /**登录smtp的账号*/
    var smtpUsername: String? = null
        set(value) {
            field = value
            if (emailFrom == null) {
                emailFrom = value
            }
        }

    /**登录smtp的密码*/
    var smtpPassword: String? = null

    /**目标收件人信息*/
    var emailTo: String? = null
        set(value) {
            field = value
            if (emailToName == null) {
                emailToName = _getNameByAddress(value)
            }
        }
    var emailToName: String? = null

    /**发件人信息*/
    var emailFrom: String? = null
        set(value) {
            field = value
            if (emailFromName == null) {
                emailFromName = _getNameByAddress(value)
            }
        }
    var emailFromName: String? = null

    /**邮件的标题*/
    var emailTitle: String = ""

    /**邮件的内容*/
    var emailContent: String = ""

    init {
        //TransportStrategy.SMTP.setOpportunisticTLS(false)
        //MailerBuilder.withTransportStrategy(TransportStrategy.SMTP)
    }

    fun _getNameByAddress(emailAddress: String?): String? {
        return if (emailAddress.isNullOrEmpty()) {
            null
        } else {
            val index = emailAddress.indexOf("@")
            if (index == -1) {
                emailAddress
            } else {
                emailAddress.substring(0, index)
            }
        }
    }

    /**构建发送邮件的客户端
     * http://www.simplejavamail.org/index.html#navigation*/
    fun buildMailer(): Mailer {
        val mailer = MailerBuilder.withSMTPServer(smtpHost, smtpPort, smtpUsername, smtpPassword)
            //传输协议
            .withTransportStrategy(TransportStrategy.SMTP)
            .withSessionTimeout(10 * 1000)
            //.clearEmailAddressCriteria()//turns off email validation
            //.resetEmailAddressCriteria()
            //.clearSignByDefaultWithSmime()
            .withProperty("mail.smtp.sendpartial", true)
            .withDebugLogging(debug)
            //.async()
            // not enough? what about this:
            //.withClusterKey(myPowerfulMailingCluster)
            .withThreadPoolSize(20) // multi-threaded batch handling
            .withConnectionPoolCoreSize(10) // reusable connection(s) / multi-server sending
            //.withCustomSSLFactoryClass("org.mypackage.MySSLFactory")
            //.withCustomSSLFactoryInstance(mySSLFactoryInstance)
            //.manyMoreOptions()
            .buildMailer()
        return mailer
    }

    /**构建邮件*/
    fun buildEmail(): Email {
        val email = EmailBuilder.startingBlank()
            .from(emailFromName, emailFrom ?: "")
            .to(emailToName, emailTo ?: "")
            /*.to("C. Cane", "candycane@candyshop.org")
            .ccWithFixedName("C. Bo group", "chocobo1@candyshop.org", "chocobo2@candyshop.org")
            .withRecipientsUsingFixedName(
                "Tasting Group",
                BCC,
                "taster1@cgroup.org;taster2@cgroup.org;tester <taster3@cgroup.org>"
            )*/
            //.cc()
            //.bcc("angcyo@126.com") //抄送
            //.withReplyTo("lollypop", "lolly.pop@othermail.com")
            .withSubject(emailTitle)
            //.withHTMLText("<img src='cid:wink1'><b>We should meet up!</b><img src='cid:wink2'>")
            .withPlainText(emailContent)
            //.withCalendar(CalendarMethod.REQUEST, iCalendarText)
            //.withEmbeddedImage("wink1", imageByteArray, "image/png")
            //.withEmbeddedImage("wink2", imageDatesource)
            //.withAttachment("invitation", pdfByteArray, "application/pdf")
            //.withAttachment("dresscode", odfDatasource)
            .withHeader("X-Priority", 5)
            //.withReturnReceiptTo()
            //.withDispositionNotificationTo("amraadmin@126.com")
            //.withBounceTo("amraadmin@126.com")
            //.signWithDomainKey(privateKeyData, "somemail.com", "selector") // DKIM
            //.signWithSmime(pkcs12Config)
            //.encryptWithSmime(x509Certificate)
            .buildEmail()
        return email
    }

    /**异常的原因, 如果有*/
    var error: Exception? = null

    var endAction: ((success: Boolean, error: Exception?) -> Unit)? = null

    /**阻塞同步执行*/
    fun doIt(): Boolean {
        error = null

        if (emailTitle.isEmpty()) {
            val exception = IllegalArgumentException("请输入邮件标题")
            error = exception
            endAction?.invoke(false, exception)
            return false
        }

        val mailer = buildMailer()
        val email = buildEmail()

        return try {
            if (mailer.validate(email)) {
                mailer.sendMail(email)
                endAction?.invoke(true, null)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            error = e
            L.e("异常:${e.message}")
            e.printStackTrace()
            endAction?.invoke(false, e)
            false
        }
    }
}

/**调用者线程处理*/
fun dslSendMail(action: DslMail.() -> Unit): Boolean {
    val dsl = DslMail()
    dsl.action()
    return dsl.doIt()
}

/**
 * 需要配置 smtp 邮件发送服务器
 * [to] 目标 xxx@126.com
 * [title] 邮件标题
 * [content] 邮件内容
 * */
fun dslSendMail(to: String, title: String, content: String, action: DslMail.() -> Unit = {}): Boolean {
    val dsl = DslMail().apply {

        //smpt配置
        val properties = beanOf(SmtpProperties::class.java)
        smtpHost = properties.host ?: smtpHost
        smtpPort = properties.port
        smtpUsername = properties.username
        smtpPassword = properties.passwrod

        //邮件配置
        emailTo = to
        emailTitle = title
        emailContent = content
        action()
    }
    return dsl.doIt()
}