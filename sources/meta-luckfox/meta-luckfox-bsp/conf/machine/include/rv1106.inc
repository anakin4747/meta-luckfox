# Common settings for Rockchip RV1106 based machines
#
# RV1106: single core ARM Cortex-A7 (NEON, VFPv4) with on-package DDR.

SOC_FAMILY = "rv1106"
require conf/machine/include/soc-family.inc

DEFAULTTUNE ?= "cortexa7thf-neon-vfpv4"
require conf/machine/include/arm/armv7a/tune-cortexa7.inc

KERNEL_IMAGETYPE ?= "zImage"

EXTRA_IMAGEDEPENDS += "virtual/bootloader"

MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-image kernel-devicetree"
MACHINE_EXTRA_RRECOMMENDS += "kernel-modules"
