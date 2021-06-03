# simplejavamail

https://github.com/bbottema/simple-java-mail

http://www.simplejavamail.org/

https://jcenter.bintray.com/org/simplejavamail/simple-java-mail/

```java
Email email = EmailBuilder.startingBlank()
    .from("lollypop", "lolly.pop@pretzelfun.com")
    .to("C. Cane", "candycane@candyshop.org")
    .cc("C. Bo <chocobo@candyshop.org>")
    .withSubject("hey")
    .withPlainText("We should meet up! ;)")
    .buildEmail();

mailer.sendMail(email);
```