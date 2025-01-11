#!/bin/bash

# openssl ecparam -name secp256k1 -genkey -noout -out private.pem

# echo "Your message" > message.txt

# keccak-256sum -l message.txt > keccaked

# openssl dgst -sha256 -sign private.pem -out signature.bin message.txt

# openssl ec -in private.pem -pubout -out public.pem

# openssl dgst -sha256 -verify public.pem -signature signature.bin message.txt

generate_keys() {
    if [ -n "$1" && -n "$2" ]; then
        local private_key_out=$1
        local public_key_out=$2
    elif [ -n "$1" ]; then
        local private_key_out=$1
        local public_key_out="public.pem"
    else 
        local private_key_out="private.pem"
        local public_key_out="public.pem"
    fi

    openssl ecparam -name secp256k1 -genkey -noout -out $private_key_out
    openssl ec -in private.pem -pubout -out public.pem $public_key_out
}

get_keys() {
    if [ -n "$1" && -n "$2" ]; then
        local private_key_out=$1
        local public_key_out=$2
    elif [ -n "$1" ]; then
        local private_key_out=$1
        local public_key_out="public.pem"
    else 
        local private_key_out="private.pem"
        local public_key_out="public.pem"
    fi
  
    paste -d "," $private_key_out $public_key_out
}

sign() {
    local message="$1"
    local message_hash=$(echo keccak-256sum -l $message)

    openssl pkeyutl -sign -inkey private.pem -in <(echo -n "$keccaked") -out signature.bin -pkeyopt digest:none -pkeyopt ec_scheme:sm2
}

verify_signature() {
    local signature=$1
    local message=$2
    local pubKey=$3

    openssl -verify public.pem -signature $signature $message 
}

generate_uuid() {
    openssl rand -hex 16 | awk '{print substr($0,1,8) "-" substr($0,9,4) "-4" substr($0,14,3) "-" substr($0,17,4) "-" substr($0,21)}' > uuid
}

associate_keys() {
    echo "foo"
}

if [ "$1" = "generate_keys" ]; then
    generate_keys $2 $3    
elif [ "$1" = "generate_uuid" ]; then
    generate_uuid
fi
