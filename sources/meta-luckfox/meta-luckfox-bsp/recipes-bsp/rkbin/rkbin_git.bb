SUMMARY = "Rockchip prebuilt loader binaries"
DESCRIPTION = "Closed source DDR init blob from rockchip-linux/rkbin, packed \
into the idblock ahead of U-Boot SPL."
HOMEPAGE = "https://github.com/rockchip-linux/rkbin"
SECTION = "bootloaders"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e1d83c54b4e0b5e550769dd04f092f6c"

PV = "0+git"
SRCREV = "3e288fe814e059dd06833495f845cab04ac20a5c"
SRC_URI = "git://github.com/rockchip-linux/rkbin.git;protocol=https;branch=master"
BB_GIT_SHALLOW = "1"

INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:rv1106 = "rv1106"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0644 ${S}/bin/rv11/${RKBIN_DDR} ${D}${datadir}/rkbin/${RKBIN_DDR}
}

FILES:${PN} = "${datadir}/rkbin"
