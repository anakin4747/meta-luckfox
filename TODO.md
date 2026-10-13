make it compliant from the start
Yocto TDD from the start

---

Makefile targets for things like pulling all SRC_URI to prepare to self host
the premirrors.

---

How does that compare to just using the downloads directory?
Well it does enforce only usiing the premirrors.

---

Write agent skills for automating work on this such as migrations to next Yocto
LTS that would make a PR and merge it if the build still worked. Only my bot
account are allowed to trigger automated builds. PR builds shall be rate
limited to that bot account as well.

Also just general bumps like using dependabot or something

---

Setting the board in a test harness that you can access or can be accessed by
CI to do hardware in the loop testing

---

oelint-adv

---

Add warning about that the distro will not be secure sinces its intended for
development. Consumers of this repo are expected to create their own secure
distro.
