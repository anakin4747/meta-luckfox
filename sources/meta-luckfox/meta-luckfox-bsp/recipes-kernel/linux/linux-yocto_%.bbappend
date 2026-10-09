# Enable linux-yocto for Rockchip RV1106 based Luckfox machines
#
# Mainline has no RV1106 support, the defconfig is a placeholder.
# TODO: add device tree

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

COMPATIBLE_MACHINE:luckfox-pico-ultra-w = "luckfox-pico-ultra-w"

# No BSP description for this board in yocto-kernel-cache
KMACHINE:luckfox-pico-ultra-w = "luckfox-pico-ultra-w"

SRC_URI:append:luckfox-pico-ultra-w = " file://defconfig"
KCONFIG_MODE:luckfox-pico-ultra-w = "alldefconfig"
