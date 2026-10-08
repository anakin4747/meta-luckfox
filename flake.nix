{
  description = "Yocto build environment for meta-luckfox";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }:
    let
      systems = [ "x86_64-linux" "aarch64-linux" ];
      forAllSystems = f: nixpkgs.lib.genAttrs systems (s: f nixpkgs.legacyPackages.${s});
    in
    {
      packages = forAllSystems (pkgs: {
        default = self.devShells.${pkgs.stdenv.hostPlatform.system}.default.passthru.fhs;
      });

      devShells = forAllSystems (pkgs:
        let
          fhs = pkgs.buildFHSEnv {
            name = "yocto-fhs";
            targetPkgs = p: with p; [

              (python3.withPackages (ps: with ps; [
                pexpect
                gitpython
                jinja2
                subunit
                websockets
                pip
              ]))

              acl
              attr
              binutils
              bzip2
              chrpath
              cpio
              debianutils
              diffstat
              file
              gawk
              gcc
              git
              glibcLocales
              gnumake
              gzip
              hostname
              iputils
              kas
              lz4
              ncurses
              ncurses.dev
              patch
              perl
              pkg-config
              rpcsvc-proto
              screen
              socat
              texinfo
              tmux
              unzip
              util-linux
              wget
              which
              xz
              zstd
            ];

            multiPkgs = p: with p; [ zlib ];
            profile = ''
              export LANG=en_US.UTF-8
              export LC_ALL=en_US.UTF-8
              export LOCALE_ARCHIVE=/usr/lib/locale/locale-archive
              # Avoid leaking Nix toolchain settings into bitbake
              unset CC CXX LD AR NIX_CFLAGS_COMPILE NIX_LDFLAGS PYTHONPATH SOURCE_DATE_EPOCH
            '';
            runScript = "bash";
          };
        in
        {
          default = fhs.env.overrideAttrs (old: {
            passthru = (old.passthru or { }) // { inherit fhs; };
          });
        });
    };
}
