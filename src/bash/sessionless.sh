#!/bin/bash

# openssl ecparam -name secp256k1 -genkey -noout -out private.pem

# echo "Your message" > message.txt

# keccak-256sum -l message.txt > keccaked

# openssl dgst -sha256 -sign private.pem -out signature.bin message.txt

# openssl ec -in private.pem -pubout -out public.pem

# openssl dgst -sha256 -verify public.pem -signature signature.bin message.txt

generate_keys() {
    local save_keys=$1
    local get_keys=$2

    openssl ecparam -name secp256k1 -genkey -noout -out private.pem
    openssl ec -in private.pem -pubout -out public.pem
}

get_keys() {
    echo "foo"
}

sign() {
    echo "foo"
}

verify_signature() {
    echo "foo"
}

generate_uuid() {
    echo "foo"
}

associate_keys() {
    echo "foo"
}

if [ "$1" = "generate_keys" ]; then
    generate_keys $2 $3    
fi
