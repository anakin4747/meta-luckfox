# Luckfox Image
#
# sources/yocto-docs/documentation/dev-manual/customizing-images.rst
# section "Customizing Images Using Custom .bb Files"

SUMMARY = "Luckfox Linux image for the Luckfox Pico boards"
LICENSE = "MIT"

IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"

inherit core-image
