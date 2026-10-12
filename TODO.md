make it compliant from the start
Yocto TDD from the start

---

WARNING: Your host glibc version (2.44) is newer than that in uninative (2.43). Disabli
ng uninative so that sstate is not corrupted.


---

inject distro version from repo git version

---

The goal is to upstream patches right?

Are you allowed to just upstream work in rockchip-linux to linux?

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


