openssl ecparam -name secp256k1 -genkey -noout -out private.pem

echo "Your message" > message.txt

keccak-256sum -l message.txt > keccaked

openssl dgst -sha256 -sign private.pem -out signature.bin message.txt

openssl ec -in private.pem -pubout -out public.pem

openssl dgst -sha256 -verify public.pem -signature signature.bin message.txt
