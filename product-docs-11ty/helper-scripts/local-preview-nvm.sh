#!/usr/bin/env sh
# shellcheck disable=SC3010,SC1091

#Run from the product-docs-11ty directory, so we can run eleventy commands
if [[ $(basename "$(pwd)") = "helper-scripts" ]]; then
  cd ..
fi

#Use the .nvmrc config
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"  # This loads nvm
nvm use 

#Run the node 16 preview script
./helper-scripts/local-preview-node16.sh