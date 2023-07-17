#!/usr/bin/env sh
# shellcheck disable=SC3010

BASE_IMAGE="node:16-alpine"

#Run from the repo root directory, as we need both gitlab config files (for parsing) and product-docs
if [[ $(basename "$(pwd)") = "helper-scripts" ]]; then
  cd ../..
fi
if [[ $(basename "$(pwd)") = "product-docs-11ty" ]]; then
  cd ..
fi

docker run -it -v "$(pwd)":/docs -p 8080:8080 ${BASE_IMAGE} sh -c "cd /docs/product-docs-11ty && ./helper-scripts/local-preview-node16.sh"
