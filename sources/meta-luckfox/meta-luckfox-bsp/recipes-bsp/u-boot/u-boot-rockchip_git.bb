SUMMARY = "Rockchip vendor U-Boot"
DESCRIPTION = "Rockchip's U-Boot fork (next-dev branch), based on U-Boot \
2017.09. Used for SoCs that mainline U-Boot does not support, such as the \
RV1106."
HOMEPAGE = "https://github.com/rockchip-linux/u-boot"
SECTION = "bootloaders"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

require recipes-bsp/u-boot/u-boot.inc

DEPENDS += "bc-native bison-native dtc-native flex-native"
DEPENDS:append:rv1106 = " rkbin xz-native"

PV = "2017.09+git"
SRCREV = "1c535d65b8509f388d09e49fb6961f49fda35a1d"
SRC_URI = "git://github.com/rockchip-linux/u-boot.git;protocol=https;branch=next-dev"

B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:luckfox-pico-ultra-w = "luckfox-pico-ultra-w"
SRC_URI:append:luckfox-pico-ultra-w = " \
    file://luckfox-pico-ultra-w_defconfig \
    file://rv1106-luckfox-pico-ultra-w.dts \
"

do_configure:prepend:luckfox-pico-ultra-w() {
    install -m 0644 ${UNPACKDIR}/luckfox-pico-ultra-w_defconfig ${S}/configs/
    install -m 0644 ${UNPACKDIR}/rv1106-luckfox-pico-ultra-w.dts ${S}/arch/arm/dts/
}

SRC_URI:append:rv1106 = " file://u-boot-rv1106.its"

UBOOT_ITB_MAX_SIZE = "262144"

do_compile:append:rv1106() {
    cd ${B}

    size=$(stat -c %s u-boot-nodtb.bin)
    lzma -9 -c u-boot-nodtb.bin > u-boot-nodtb.bin.lzma
    perl -e 'print pack("V", shift)' $size >> u-boot-nodtb.bin.lzma
    openssl dgst -sha256 -binary -out u-boot-nodtb.bin.digest u-boot-nodtb.bin

    cp ${UNPACKDIR}/u-boot-rv1106.its .
    ./tools/mkimage -f u-boot-rv1106.its -E -p 0x1400 u-boot.itb
    if [ $(stat -c %s u-boot.itb) -gt ${UBOOT_ITB_MAX_SIZE} ]; then
        bbfatal "u-boot.itb exceeds ${UBOOT_ITB_MAX_SIZE} bytes"
    fi
    cp u-boot.itb uboot.img
    truncate -s ${UBOOT_ITB_MAX_SIZE} uboot.img

    ./tools/mkimage -n rv1106 -T rksd \
        -d ${STAGING_DATADIR}/rkbin/${RKBIN_DDR}:spl/u-boot-spl.bin idblock.img
}

do_deploy:append:rv1106() {
    install -m 0644 ${B}/idblock.img ${DEPLOYDIR}/idblock.img
    install -m 0644 ${B}/uboot.img ${DEPLOYDIR}/uboot.img
}
