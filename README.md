
# meta-luckfox

Kas-based yocto build for Luckfox Pico Ultra W

References:
- https://github.com/LuckfoxTECH/Luckfox-Pico-docs
- https://wiki.luckfox.com/Luckfox-Pico-RV1106/Getting-Started
- sources/yocto-docs/

## Layers

### `meta-luckfox-bsp`

`meta-luckfox-bsp` is the BSP layer

Always follow `sources/yocto-docs/documentation/bsp-guide/bsp.rst` when working
on the BSP layer

#### Machines

- `luckfox-pico-ultra-w`: Luckfox Pico Ultra W (Rockchip RV1106G3, Cortex-A7,
  8GB eMMC, 10/100 Ethernet with PoE, USB OTG + host, SDIO
  Wi-Fi/BT). Shared SoC settings live in `conf/machine/include/rv1106.inc`

The devicetree shall be source controlled with the machine layer
The defconfig shall be source controlled with the machine layer

### `meta-luckfox-distro`

`meta-luckfox-distro` is the example distro layer for this board

Always follow
`sources/yocto-docs/documentation/dev-manual/custom-distribution.rst`
when working on the distro layer

#### Images

Always follow
`sources/yocto-docs/documentation/dev-manual/customizing-images.rst` section
`Customizing Images Using Custom .bb Files` when working on the custom image

## Building

All builds run inside the cqfd container (`.cqfd/docker/Dockerfile`) through
the Makefile. Run `make help` to list the targets.

```sh
export DL_DIR=/path/to/downloads
export SSTATE_DIR=/path/to/sstate-cache
make build
```

`make build` lints the commit messages with cocogitto first, then runs
`scripts/ci-build`, which picks the mode from HEAD:

- **review**: HEAD has no annotated `v*` tag. Uses `DL_DIR` and `SSTATE_DIR`
  as given. `DISTRO_VERSION` is `git describe --tags --always --dirty`
- **release**: HEAD has an annotated `v*` tag. Uses `DL_DIR`, but builds
  from an empty temporary sstate-cache. `DISTRO_VERSION` is the tag. Writes
  the changelog for the tag to `CHANGELOG-release.md` and packages the files
  listed in `.cqfdrc` into `meta_luckfox-<tag>.tar.gz` plus a `.sha256`

`DL_DIR` and `SSTATE_DIR` are mounted into the container at the same paths.
They must be absolute paths with no whitespace.

## Updating Dependencies

```sh
make lock
```

## Releasing

```sh
scripts/mkrelease
```

`scripts/mkrelease` runs `cog bump --auto`, which updates `CHANGELOG.md`,
commits it, and creates an annotated `vX.Y.Z` tag. It then pushes the commit
and tag together to `origin`, and CI builds and publishes the release.

## CI

`.github/workflows/ci.yml` runs on every push. Fork pull requests never
trigger it. Every job only runs when `anakin4747` is both the actor and the
triggering actor.

1. `classify` (GitHub-hosted) works out the mode. It rejects tags that are
   lightweight or not `vX.Y.Z`
2. `build` (self-hosted, labels `self-hosted, linux, yocto`) runs `make build`
3. `publish` (GitHub-hosted, the only job with `contents: write`) runs only
   if the build succeeded. It checks again that the tag hasn't moved and
   creates the GitHub release with the changelog as the release notes

### Runner requirements

- Labels: `self-hosted`, `linux`, `yocto`. Use a dedicated machine or VM;
  ephemeral runners are preferred
- Docker, with the runner user in the `docker` group. **This is root
  equivalent, and cqfd runs containers with `--privileged`**, so the runner
  must not hold anything else of value
- `DL_DIR` and `SSTATE_DIR` set in the runner's service environment, pointing
  at persistent directories that the runner user can write

### Repository settings

- Actions > General: require approval for all outside collaborators;
  workflow permissions read-only by default
- Runner group: restricted to this repository
- Tag ruleset on `v*`: only the owner may create, update, or delete
- Branch ruleset on the default branch: no force pushes or deletion

## Hardware

Summary of `docs/schematic.pdf` (Luckfox Pico Ultra W; the "Ultra" variant is
the same board without Wi-Fi).

### SoC

- Rockchip RV1106G3 (U2), single core ARM Cortex-A7, DDR on package
- Clocks: 24MHz main crystal (Y1), 32.768kHz RTC crystal (Y2)
- Rails: `VDD_ARM`, `VDD_0V9` (logic/NPU/VEPU), `VCC_DDR` (1.8V),
  `VCC_1V8`, `VCC_3V3`, `VCC3V3_RTC`, from a 3-channel DCDC (U1) fed by
  `VCC5V0_SYS`

### IO voltage domains

| Domain | Voltage | Functions                                          |
|--------|---------|----------------------------------------------------|
| PMUIO  | 3.3V    | GPIO0: UART/PWM/I2C                                |
| VCCIO1 | 3.3V    | GPIO1_A/B: UART/I2C/PWM/JTAG                       |
| VCCIO2 | 1.8V    | SARADC_IN0/IN1 (GPIO4_C0/C1)                       |
| VCCIO3 | flash   | eMMC/FSPI (GPIO4_A/B)                              |
| VCCIO4 | 3.3V    | SDMMC (GPIO3_A), used for the Wi-Fi module         |
| VCCIO5 | 3.3V    | GPIO2_A/B: SDIO/I2S/LCDC/UART/PWM/I2C              |
| VCCIO6 | 3.3V    | GPIO1_C/D: LCDC/CIF/SPI/UART/I2C/PWM               |
| VCCIO7 | 1.8V    | MIPI CSI/LVDS RX (GPI3_B/C, input only), GPIO3_C/D |

### Storage

- 8GB eMMC (U7), 8-bit bus on EMMC_D0..D7/CMD/CLK, `FLASH_ALE/EMMC_RST`
- No SD card slot (SDMMC is wired to the Wi-Fi module)

### Wireless (W variant only)

- SKI.WB800DCS Wi-Fi/BT module (U6) with on-board antenna
- Wi-Fi over SDIO on SDMMC (`SD_D0..D3`, `SD_CMD`, `SD_CLK`)
- BT over UART1_M0 (GPIO1_A3 TX / GPIO1_A4 RX, GPIO0_A5 RTS / GPIO0_A6 CTS)
- `WL_EN`: GPIO1_A2, `HOST_WAKE_BT`: GPIO0_A2
- `WL_HOST_WAKE` and `BT_WAKE_HOST` go through level shifters (U9) to
  GPI3_B0/GPI3_B1

### Ethernet

- 10/100 PHY built into the SoC (FEPHY), RJ45 jack with magnetics (RJ1)
- PoE: center taps go to the PoE header (P4), PoE input on POUT1

### USB

- One USB 2.0 controller (`USB_DP`/`USB_DM`), shared through a USB switch (U10)
  - USB Type-C (H5), with 5.1K pull-downs on CC1/CC2 (device/OTG)
  - USB Type-A (J2), host
- VBUS detection on `USB_VBUSDET` (`USB_DET_IN`)

### Audio

- Audio codec built into the SoC
- MIC0: on-board MEMS microphone (MIC1)
- MIC1 and `MICBIAS` routed to the GPIO header
- `LINEOUT` drives an amplifier (U3) to the speaker connector (H4, MX1.25 2P)

### Camera

- MIPI CSI-2 2-lane, 20-pin 0.5mm FPC (H1)
- `MIPI_I2C` (I2C4_M2: GPIO3_C7 SCL / GPIO3_D0 SDA), `MIPI_CLK0_OUT` as MCLK,
  reset on GPIO3_C6

### Display

- RGB666 parallel LCD, 40-pin 0.5mm FPC (H2)
  - `RGB_DB0..17` on GPIO1_C*/GPIO1_D*/GPIO2_A*/GPIO2_B*
  - `RGB_CLK`, `RGB_DE`, `RGB_HSYNC`, `RGB_VSYNC` on GPIO1_D0..D3
  - SPI init/control lines: GPIO0_A3, GPIO0_A4
- Backlight boost driver (U4), enabled by GPIO3_D3, ~40mA
- Touch on the same FPC: `TP_SDA`/`TP_SCL` through level shifters (U8) on
  GPIO3_D2/GPIO3_D1

### Debug and control

- Debug UART: UART2_M1 (GPIO1_B2 TX / GPIO1_B3 RX), on the GPIO header
- RECOVERY key on SARADC_IN0 (GPIO4_C0, must always be pulled up)
- Reset key (nPOR)
- User LED (LED1)
- 2-pin SH1.0 header (H3)

### GPIO headers

P1 (2x13, left):

| Pin | Signal    | Pin | Signal    |
|-----|-----------|-----|-----------|
| 1   | VCC_3V3   | 2   | 5V        |
| 3   | GPIO1_A0  | 4   | 5V        |
| 5   | GPIO1_A1  | 6   | GND       |
| 7   | GPIO1_B0  | 8   | GPIO1_B1  |
| 9   | GND       | 10  | GPIO1_D0  |
| 11  | GPIO1_D1  | 12  | VCC_3V3   |
| 13  | GPIO1_C2  | 14  | GPIO1_C3  |
| 15  | GPIO1_C1  | 16  | GND       |
| 17  | GPIO1_B2  | 18  | GPIO1_B3  |
| 19  | GPIO1_C6  | 20  | GND       |
| 21  | GPIO2_A7  | 22  | GPIO2_A6  |
| 23  | GND       | 24  | GPIO1_D3  |
| 25  | GPIO1_C0  | 26  | GPIO1_D2  |

P2 (2x13, right):

| Pin | Signal         | Pin | Signal    |
|-----|----------------|-----|-----------|
| 1   | GND            | 2   | GPIO4_C0  |
| 3   | GPIO4_C1       | 4   | GND       |
| 5   | CODEC_MICBIAS  | 6   | CODEC_MIC1P |
| 7   | CODEC_MIC1N    | 8   | GND       |
| 9   | GPIO1_C7       | 10  | GPIO2_B0  |
| 11  | GPIO2_B1       | 12  | GND       |
| 13  | GND            | 14  | VCC_3V3   |
| 15  | GND            | 16  | GPIO1_C4  |
| 17  | GPIO1_C5       | 18  | GPIO2_A1  |
| 19  | GPIO2_A0       | 20  | GPIO2_A5  |
| 21  | GPIO2_A4       | 22  | GPIO2_A2  |
| 23  | GPIO2_A3       | 24  | GND       |
| 25  | 1V8            | 26  | GND       |

The header pin numbering is read from the net order in the schematic; check it
against the official pinout before relying on it.

