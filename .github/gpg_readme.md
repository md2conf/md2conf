# Generating GPG

https://central.sonatype.org/publish/requirements/gpg/

### Dealing with Expired Keys
https://central.sonatype.org/publish/requirements/gpg/#dealing-with-expired-keys


# How to Sign and Release to The Central Repository with GitHub Actions

GitHub allows automated builds using [GitHub Actions](https://help.github.com/en/actions). A commonly asked question is how to release artifacts (packaged Java jars) built by Maven and Gradle to [The Central Repository](https://search.maven.org/). The GitHub Actions documentation provides only part of the answer.

So, first, configure your Maven project for staging artifacts to The Central Repository, by reading through [Configuring Your Project for Deployment](https://help.sonatype.com/repomanager2/staging-releases/configuring-your-project-for-deployment) and following those steps. Please make sure that the maven-gpg-plugin is configured to prevent `gpg` from using PIN entry programs, as follows:
```xml
<configuration>
  <gpgArguments>
      <arg>--pinentry-mode</arg>
      <arg>loopback</arg>
  </gpgArguments>
</configuration>
```
At this point, you should be able to manually stage your artifacts to The Central Repository.

Next, set up a basic GitHub Actions workflow to build your project. Take a look at [Publishing Java packages with Maven]( https://help.github.com/en/actions/language-and-framework-guides/publishing-java-packages-with-maven), and complete all the steps there.

At this point, you will find that you are missing one step - being able to sign your Maven-built jar files within your GitHub Actions workflow. You can follow the steps below to sign artifacts in GitHub actions. The trick involves loading in your private key into GitHub Actions using the gpg command-line commands.

1. Export your gpg private key from the system that you have created it.
    1. Find your key-id
    2. Export the gpg secret key to an ASCII file using `gpg --export-secret-keys -a <key-id> | tr '\n' ',' | sed -e 's#,#\\n#g' > secret.txt`
    3. Note `tr '\n' ',' | sed -e 's#,#\\n#g'` will replace all newlines with a literal "\n" until everything is on a single line
2. Set up [GitHub Actions secrets](https://help.github.com/en/actions/configuring-and-managing-workflows/creating-and-storing-encrypted-secrets)
    1. Create a secret called `OSSRH_GPG_SECRET_KEY` using the text from your edited `secret.txt` file (the whole text should be in a single line)
    2. Create a secret called `OSSRH_GPG_SECRET_KEY_PASSWORD` containing the password for your gpg secret key
3. Create a GitHub Actions step to install the gpg secret key
    1. Add an action similar to:
        ```yaml
        - id: install-secret-key
          name: Install gpg secret key
          run: |
            cat <(echo -e "${{ secrets.OSSRH_GPG_SECRET_KEY }}") | gpg --batch --import
            gpg --list-secret-keys --keyid-format LONG
        ```
    2. Verify that the secret key is shown in the GitHub Actions logs
    3. You can remove the output from list secret keys if you are confident that this action will work, but it is better to leave it in there
4. Bring it all together, and create a GitHub Actions step to publish
    1. Add an action similar to:
        ```yaml
        - id: publish-to-central
          name: Publish to Central Repository
          env:
            MAVEN_USERNAME: ${{ secrets.OSSRH_USERNAME }}
            MAVEN_PASSWORD: ${{ secrets.OSSRH_TOKEN }}
          run: |
            mvn \
              --no-transfer-progress \
              --batch-mode \
              -Dgpg.passphrase=${{ secrets.OSSRH_GPG_SECRET_KEY_PASSWORD }} \
              clean deploy
        ```
    2. After a couple of hours, verify that the artifact got published to The Central Repository
