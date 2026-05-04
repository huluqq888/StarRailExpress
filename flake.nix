{
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/release-25.11";

  outputs = {self, ...} @ inputs: let
    javaVersion = 21;

    supportedSystems = [
      "x86_64-linux"
      "aarch64-linux"
      "x86_64-darwin"
      "aarch64-darwin"
    ];
    forEachSupportedSystem = f:
      inputs.nixpkgs.lib.genAttrs supportedSystems (
        system:
          f {
            pkgs = import inputs.nixpkgs {
              inherit system;
              overlays = [inputs.self.overlays.default];
            };
          }
      );
  in {
    overlays.default = final: prev: {
      jdk = prev."jdk${toString javaVersion}";
    };

    devShells = forEachSupportedSystem (
      {pkgs}: {
        default = pkgs.mkShellNoCC {
          packages = with pkgs; [
            jdk
          ];
        };
      }
    );
  };
}
