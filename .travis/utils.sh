#!/bin/bash
# Abort script on first failure
set -e

function backupMavenRepo() {
  if [ "$1" != "porcelain" ];
  then
    mkdir /tmp/cache-trick
    mv "$HOME/.m2/repository/org/ldp4j" /tmp/cache-trick/
  else
    echo "Skipped Maven Repo backup"
  fi
}

function restoreMavenRepo() {
  if [ "$1" != "porcelain" ];
  then
    mv /tmp/cache-trick/ldp4j "$HOME/.m2/repository/org/"
  else
    echo "Skipped Maven Repo restoration"
  fi
}

function fail() {
  echo "ERROR: Unknown command '${1}'."
  return 1
}

function beginSensibleBlock() {
  set +x
  if [ "${DEBUG}" = "trace" ];
  then
    set -v
  fi
}

function endSensibleBlock() {
  set +v
  if [ "${DEBUG}" = "trace" ];
  then
    set -x
  fi
}

function unshallowRepo() {
  if [ "$1" != "porcelain" ];
  then
    if [[ -a .git/shallow ]];
    then
      echo "Unshallowing Git repository..."
      set +e
      git fetch --unshallow --verbose;
      set -e
      error=$?
      if [[ $error -ne 0 ]];
      then
        echo "Unshallowing failed with $error status code"
        git --version
        return $error
      fi
    fi
  else
    echo "Skipped Git repository unshallowing"
  fi
}

function decryptKeys() {
  endSensibleBlock

  if [ "$#" != "2" ];
  then
    echo "ERROR: No encryption password specified."
    return 2
  fi

  if [ "$1" != "porcelain" ];
  then
    beginSensibleBlock
    echo "Decrypting private key..."
    openssl aes-256-cbc -pass pass:"$2" -in target/config/ci/secring.gpg.enc -out local.secring.gpg -d
    echo "Decrypting public key..."
    openssl aes-256-cbc -pass pass:"$2" -in target/config/ci/pubring.gpg.enc -out local.pubring.gpg -d
    endSensibleBlock
  else
    echo "Skipped key decription"
  fi
}

function runUtility() {
  endSensibleBlock
  
  mode=$1
  if [ "$mode" != "porcelain" ];
  then
    mode=${CI}
  fi

  check=$1
  shift
  if [ "$check" = "porcelain" ];
  then
    action=$1
    shift
  else
    action=$check
  fi
  
  case "$action" in
    backup-maven-repo )
      backupMavenRepo "$mode"
      ;;
    restore-maven-repo)
      restoreMavenRepo "$mode"
      ;;
    prepare-keys      )
     beginSensibleBlock
     decryptKeys "$mode" "$@"
      ;;
    prepare-repo      )
      unshallowRepo "$mode"
      ;;
    *                 )
      fail "$action"
      ;;
  esac
}

function skipBuild() {
  echo "Skipping build..."
}

case "${CI}" in
  skip ) 
    skipBuild 
    ;;
  *    )
    beginSensibleBlock
    runUtility "$@" 
    ;;
esac
