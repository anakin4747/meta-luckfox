# Board defconfig for Luckfox machines
#
# Upstream U-Boot has no RV1106 support, the defconfig is a placeholder.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:luckfox-pico-ultra-w = " file://luckfox-pico-ultra-w_defconfig"

do_configure:prepend:luckfox-pico-ultra-w() {
    install -m 0644 ${UNPACKDIR}/luckfox-pico-ultra-w_defconfig ${S}/configs/
}
