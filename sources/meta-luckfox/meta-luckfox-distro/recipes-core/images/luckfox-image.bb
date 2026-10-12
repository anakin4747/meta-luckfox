# Luckfox Image
#
# sources/yocto-docs/documentation/dev-manual/customizing-images.rst
# section "Customizing Images Using Custom .bb Files"

SUMMARY = "Luckfox Linux image for the Luckfox Pico boards"
LICENSE = "MIT"

IMAGE_INSTALL = "packagegroup-core-boot vim"

# TODO: warn users that this distro is not for production and not intended for
# secure systems, it is strictly for development and board bring up
IMAGE_FEATURES:append = " \
    allow-empty-password \
    allow-root-login \
    empty-root-password \
    post-install-logging  \
"

inherit core-image
