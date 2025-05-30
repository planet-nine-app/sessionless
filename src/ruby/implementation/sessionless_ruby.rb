require 'ecdsa'
require 'digest'
require 'securerandom'
require 'base64'

# Sessionless Authentication Protocol (SLAP) Ruby Implementation
# Based on the protocol from https://github.com/planet-nine-app/sessionless
#
# This implementation provides a transport-agnostic, multi-platform authentication
# protocol using secp256k1 elliptic curve cryptography (same as Bitcoin/Ethereum)

class Sessionless
  # secp256k1 curve parameters
  CURVE = ECDSA::Group::Secp256k1
  
  class << self
    # Client Methods
    
    # Generate a new private/public key pair from a seed phrase
    # @param seed_phrase [String] A seed phrase to generate deterministic keys
    # @return [Hash] Contains :private_key and :public_key as hex strings
    def generate_keys(seed_phrase)
      # Hash the seed phrase to create a deterministic private key
      private_key_bytes = Digest::SHA256.digest(seed_phrase)
      private_key_int = private_key_bytes.unpack1('H*').to_i(16)
      
      # Ensure private key is within valid range for secp256k1
      private_key_int = private_key_int % CURVE.order
      
      # Generate public key from private key
      public_key_point = CURVE.generator * private_key_int
      
      # Convert to compressed public key format (33 bytes)
      public_key_compressed = ECDSA::Format::PointOctetString.encode(
        public_key_point, 
        compression: true
      )
      
      {
        private_key: private_key_int.to_s(16).rjust(64, '0'),
        public_key: public_key_compressed.unpack1('H*')
      }
    end
    
    # Sign a message with a private key
    # @param message [String] The message to sign
    # @param private_key_hex [String] Private key as hex string
    # @return [String] Base64 encoded signature
    def sign(message, private_key_hex)
      private_key_int = private_key_hex.to_i(16)
      message_hash = Digest::SHA256.digest(message)
      
      # Generate deterministic k value (RFC 6979)
      k = generate_deterministic_k(private_key_int, message_hash)
      
      # Create signature
      signature = ECDSA.sign(CURVE, private_key_int, message_hash, k)
      
      # Encode signature in DER format
      der_signature = ECDSA::Format::SignatureDerString.encode(signature)
      Base64.strict_encode64(der_signature)
    end
    
    # Server Methods
    
    # Verify a signature with a public key
    # @param signature_base64 [String] Base64 encoded signature
    # @param message [String] Original message that was signed
    # @param public_key_hex [String] Public key as hex string
    # @return [Boolean] True if signature is valid
    def verify_signature(signature_base64, message, public_key_hex)
      begin
        # Decode signature
        der_signature = Base64.strict_decode64(signature_base64)
        signature = ECDSA::Format::SignatureDerString.decode(der_signature)
        
        # Decode public key
        public_key_bytes = [public_key_hex].pack('H*')
        public_key_point = ECDSA::Format::PointOctetString.decode(
          public_key_bytes, 
          CURVE
        )
        
        # Hash message
        message_hash = Digest::SHA256.digest(message)
        
        # Verify signature
        ECDSA.valid_signature?(public_key_point, message_hash, signature)
      rescue => e
        # Any error in verification means invalid signature
        false
      end
    end
    
    # Generate a UUID v4 for user identification
    # @return [String] UUID v4 string
    def generate_uuid
      SecureRandom.uuid
    end
    
    # Optional Methods
    
    # Associate two signatures to link public keys (convenience method)
    # @param signature1_base64 [String] First signature
    # @param signature2_base64 [String] Second signature  
    # @param message [String] Message that both signatures signed
    # @param public_key1_hex [String] First public key
    # @param public_key2_hex [String] Second public key
    # @return [Boolean] True if both signatures are valid
    def associate(signature1_base64, signature2_base64, message, public_key1_hex, public_key2_hex)
      verify_signature(signature1_base64, message, public_key1_hex) &&
        verify_signature(signature2_base64, message, public_key2_hex)
    end
    
    private
    
    # Generate deterministic k value for ECDSA signing (RFC 6979)
    # This prevents signature malleability and ensures deterministic signatures
    def generate_deterministic_k(private_key, message_hash)
      # Simplified deterministic k generation
      # In production, use proper RFC 6979 implementation
      combined = [private_key.to_s(16), message_hash.unpack1('H*')].join
      k_bytes = Digest::SHA256.digest(combined)
      k = k_bytes.unpack1('H*').to_i(16)
      k % CURVE.order
    end
  end
end

# Example usage and test cases
if __FILE__ == $0
  puts "Sessionless Protocol Ruby Implementation Demo"
  puts "=" * 50
  
  # 1. Generate keys
  puts "\n1. Generating keys from seed phrase..."
  seed_phrase = "my secret seed phrase"
  keys = Sessionless.generate_keys(seed_phrase)
  
  puts "Private Key: #{keys[:private_key]}"
  puts "Public Key:  #{keys[:public_key]}"
  
  # 2. Sign a message
  puts "\n2. Signing a message..."
  message = "Hello, Sessionless World!"
  signature = Sessionless.sign(message, keys[:private_key])
  
  puts "Message:   #{message}"
  puts "Signature: #{signature}"
  
  # 3. Verify signature
  puts "\n3. Verifying signature..."
  is_valid = Sessionless.verify_signature(signature, message, keys[:public_key])
  
  puts "Valid signature: #{is_valid}"
  
  # 4. Generate UUID
  puts "\n4. Generating UUID for user..."
  user_uuid = Sessionless.generate_uuid
  puts "User UUID: #{user_uuid}"
  
  # 5. Test with wrong signature
  puts "\n5. Testing with tampered message..."
  tampered_message = "Hello, Tampered World!"
  is_valid_tampered = Sessionless.verify_signature(signature, tampered_message, keys[:public_key])
  puts "Valid signature for tampered message: #{is_valid_tampered}"
  
  # 6. Demonstrate deterministic key generation
  puts "\n6. Demonstrating deterministic key generation..."
  keys2 = Sessionless.generate_keys(seed_phrase)
  puts "Keys are deterministic: #{keys[:private_key] == keys2[:private_key]}"
  
  puts "\n" + "=" * 50
  puts "Demo completed successfully!"
end