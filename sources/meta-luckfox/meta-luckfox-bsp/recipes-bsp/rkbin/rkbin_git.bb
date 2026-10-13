SUMMARY = "Rockchip prebuilt loader binaries"
DESCRIPTION = "Closed source DDR init blob from rockchip-linux/rkbin, packed \
into the idblock ahead of U-Boot SPL, and the USB download loader used by \
rkdeveloptool/upgrade_tool to flash the board from MaskROM mode."
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

inherit deploy

B = "${S}"

# Stable name for the download loader in DEPLOY_DIR_IMAGE, linked to the
# versioned file named by [OUTPUT] PATH in RKBIN_LOADER_INI
RKBIN_LOADER_LINK ?= "${SOC_FAMILY}_download.bin"

do_configure[noexec] = "1"

# Print the value of key $2 in section [$1] of the loader ini
rkbin_ini_get() {
    sed -n "/^\[$1\]/,/^\[/{s/^$2=//p}" ${S}/${RKBIN_LOADER_INI} | tr -d '\r'
}

do_compile() {
    if [ -z "${RKBIN_LOADER_INI}" ]; then
        bbfatal "RKBIN_LOADER_INI is not set"
    fi
    if [ ! -f "${S}/${RKBIN_LOADER_INI}" ]; then
        bbfatal "loader ini not found: ${RKBIN_LOADER_INI}"
    fi

    # boot_merger is a prebuilt x86_64 Linux binary shipped in rkbin
    case "${BUILD_ARCH}" in
    x86_64) ;;
    *) bbfatal "rkbin tools/boot_merger only runs on x86_64 build hosts, not ${BUILD_ARCH}" ;;
    esac

    # The flashing loader must init DDR the same way as the idblock
    ini_ddr=$(rkbin_ini_get LOADER_OPTION FlashData)
    if [ "$(basename "$ini_ddr")" != "${RKBIN_DDR}" ]; then
        bbfatal "${RKBIN_LOADER_INI} uses DDR blob $(basename "$ini_ddr"), but RKBIN_DDR is ${RKBIN_DDR}"
    fi

    loader=$(rkbin_ini_get OUTPUT PATH)
    if [ -z "$loader" ]; then
        bbfatal "no [OUTPUT] PATH in ${RKBIN_LOADER_INI}"
    fi

    cd ${S}
    rm -f "$loader"
    ./tools/boot_merger ${RKBIN_LOADER_INI} || bbfatal "boot_merger ${RKBIN_LOADER_INI} failed"
    if [ ! -f "$loader" ]; then
        bbfatal "boot_merger did not produce $loader"
    fi
}

do_install() {
    install -D -m 0644 ${S}/bin/rv11/${RKBIN_DDR} ${D}${datadir}/rkbin/${RKBIN_DDR}
}

do_deploy() {
    loader=$(rkbin_ini_get OUTPUT PATH)
    install -m 0644 ${S}/$loader ${DEPLOYDIR}/$loader
    ln -sf $loader ${DEPLOYDIR}/${RKBIN_LOADER_LINK}
}
addtask deploy after do_compile before do_build

FILES:${PN} = "${datadir}/rkbin"
