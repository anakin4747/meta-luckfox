# Enable linux-yocto for Rockchip RV1106 based Luckfox machines
#
# TODO: add defconfig / config fragments and device tree

COMPATIBLE_MACHINE:luckfox-pico-ultra-w = "luckfox-pico-ultra-w"

# No BSP description for this board in yocto-kernel-cache
KMACHINE:luckfox-pico-ultra-w = "luckfox-pico-ultra-w"
