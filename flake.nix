{
  description = "Java Project Environment with Maven and Launch4j support";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = nixpkgs.legacyPackages.${system};
      jdk25 = pkgs.javaPackages.compiler.openjdk25;

      fhs = pkgs.buildFHSEnv {
        name = "java-maven-env";
        targetPkgs = pkgs: (with pkgs; [
          jdk25
          maven
          zlib
          freetype
        ]);

        profile = ''
          export JAVA_HOME=${jdk25}
        '';

        runScript = "bash";
      };
    in
    {
      devShells.${system}.default = fhs.env;
    };
}