#!/bin/bash

# Get User Input
read -p '[>] Please enter domain name for server SSL certificate: ' certdom

# Generate Self-Signed Cert
openssl req -nodes -x509 -newkey rsa:2048 -keyout ../data/empire-priv.key -out ../data/empire-chain.pem -days 365 -subj "/C=US/O=Empire/CN=${certdom}"
openssl x509 -inform PEM -outform DER -in ../data/empire-chain.pem -out ../data/empire.crt
# Print Cert Location
echo '[*] Certificate written to ../data/empire-chain.pem'
echo '[*] Private key written to ../data/empire-priv.key'
echo '[*] Android Certificate written to ../data/empire.crt'
