# How to release

This project uses https://github.com/qoomon/maven-git-versioning-extension, so the released version is
derived from the git tag (`v0.6.3` -> `0.6.3`). Do not edit `<version>` in any `pom.xml`.

Artifacts are published to Maven Central through the Sonatype Central Portal by the
`central-publishing-maven-plugin` (the `release` profile of the root `pom.xml`). 

## One-time setup

1. Sonatype Central Portal (https://central.sonatype.com): the `io.github.md2conf` namespace must be
   verified for the publishing account. Generate a user token in the account settings and store it in
   this repository as the variable `OSSRH_USERNAME` and the secret `OSSRH_TOKEN`. Those names are
   legacy - the values are the new Portal token username / password pair, not Sonatype Jira credentials.
2. GPG: the signing key must be published to a keyserver; the ASCII-armored private key and its
   passphrase go to the `OSSRH_GPG_SECRET_KEY` and `OSSRH_GPG_SECRET_KEY_PASSWORD` secrets
   (see `.github/gpg_readme.md`).
3. Credentials are read from the `settings.xml` server with the id `central`, which
   `actions/setup-java` generates in the release job from `OSSRH_USERNAME` / `OSSRH_TOKEN`.

## Steps

1. Create a new git tag in semver format and push it: `git tag v0.6.3 && git push origin v0.6.3`.
2. Wait for the `Release` workflow to finish. It runs `mvn -P release,integration-tests clean deploy`,
   which signs the artifacts, uploads one bundle to the Central Portal, waits for the validation and
   publishes it automatically (`autoPublish`).
3. Check the deployment status in https://central.sonatype.com/publishing/deployments if the job fails -
   validation errors are reported there.
4. Mark the GitHub release as the latest release. The artifacts show up on
   https://search.maven.org shortly after the publishing finishes.

To keep a deployment for a manual review in the Portal UI instead of releasing it immediately, set
`<autoPublish>false</autoPublish>` in the `release` profile and publish it from
https://central.sonatype.com/publishing/deployments by hand.
