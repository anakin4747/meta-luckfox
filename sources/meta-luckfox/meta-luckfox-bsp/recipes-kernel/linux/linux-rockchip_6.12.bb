SUMMARY = "Rockchip vendor Linux kernel"
DESCRIPTION = "Rockchip's Linux kernel fork (develop-6.12 branch). Used for \
SoCs that mainline Linux does not support, such as the RV1106."
HOMEPAGE = "https://github.com/rockchip-linux/kernel"
SECTION = "kernel"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel

LINUX_VERSION = "6.12.69"
PV = "${LINUX_VERSION}+git"
SRCREV = "470f9dccbdc42e7b8a824d0a5c5640a10e9457d2"
SRC_URI = "git://github.com/rockchip-linux/kernel.git;protocol=https;branch=develop-6.12"

COMPATIBLE_MACHINE = "^$"

COMPATIBLE_MACHINE:luckfox-pico-ultra-w = "luckfox-pico-ultra-w"
SRC_URI:append:luckfox-pico-ultra-w = " \
    file://0001-ARM-dts-rockchip-add-Luckfox-Pico-Ultra-W.patch \
    file://defconfig \
"

# The vendor U-Boot boot_fit command loads a FIT boot.img from the "boot"
# partition. It takes the kernel dtb from a Rockchip resource image in the
# FIT, so pack the dtb both as the FIT fdt and inside the resource image.
SRC_URI:append:rv1106 = " file://boot.its"
DEPENDS:append:rv1106 = " u-boot-tools-native"

do_deploy:append:rv1106() {
    set -- ${KERNEL_DEVICETREE}
    if [ $# -ne 1 ]; then
        bbfatal "boot.img needs exactly one KERNEL_DEVICETREE, got: $*"
    fi
    dtb="${B}/arch/${ARCH}/boot/dts/$1"

    fitdir="${B}/fit"
    rm -rf "$fitdir"
    mkdir -p "$fitdir"
    cd "$fitdir"

    ${B}/scripts/resource_tool --image=resource.img "$dtb"
    cp "$dtb" fdt
    cp "${B}/${KERNEL_OUTPUT_DIR}/zImage" kernel
    cp resource.img resource
    cp ${UNPACKDIR}/boot.its .

    uboot-mkimage -E -B 0x200 -p 0x800 -f boot.its boot.img
    install -m 0644 boot.img ${DEPLOYDIR}/boot.img
}
