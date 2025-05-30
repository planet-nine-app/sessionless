require 'ecdsa'
require 'digest'
require 'securerandom'
require 'base64'
require 'json'

# Sessionless Authentication Protocol (SLAP) Ruby Implementation
# A comprehensive implementation with error handling and additional features

module Sessionless
  class Error < StandardError; end
  class KeyGenerationError < Error; end
  class SigningError < Error; end
  class VerificationError < Error; end
  class InvalidKeyError < Error; end
  
  # Configuration constants
  CURVE = ECDSA::Group::Secp256k1
  SIGNATURE_ENCODING = 'base64'
  KEY_ENCODING = 'hex'
  
  class KeyPair
    attr_reader :private_key, :public_key, :private_key_hex, :public_key_hex
    
    def initialize(private_key_hex, public_key_hex)
      @private_key_hex = private_key_hex
      @public_key_hex = public_key_hex
      @private_key = private_key_hex.to_i(16)
      
      # Decode public key point
      public_key_bytes = [public_key_hex].pack('H*')
      @public_key = ECDSA::Format::PointOctetString.decode(public_key_bytes, CURVE)
    rescue => e
      raise InvalidKeyError, "Invalid key format: #{e.message}"
    end
    
    def to_h
      {
        private_key: @private_key_hex,
        public_key: @public_key_hex
      }
    end
    
    def to_json(*args)
      to_h.to_json(*args)
    end
  end
  
  class Signature
    attr_reader :signature_base64, :message, :timestamp
    
    def initialize(signature_base64, message, timestamp = nil)
      @signature_base64 = signature_base64
      @message = message
      @timestamp = timestamp || Time.now.to_f
    end
    
    def to_h
      {
        signature: @signature_base64,
        message: @message,
        timestamp: @timestamp
      }
    end
    
    def to_json(*args)
      to_h.to_json(*args)
    end
  end
  
  class Client
    # Generate a new private/public key pair from a seed phrase
    # @param seed_phrase [String] A seed phrase to generate deterministic keys
    # @return [KeyPair] Contains private and public keys
    def self.generate_keys(seed_phrase)
      raise ArgumentError, "Seed phrase cannot be empty" if seed_phrase.nil? || seed_phrase.empty?
      
      begin
        # Hash the seed phrase to create a deterministic private key
        private_key_bytes = Digest::SHA256.digest(seed_phrase)
        private_key_int = private_key_bytes.unpack1('H*').to_i(16)
        
        # Ensure private key is within valid range for secp256k1
        private_key_int = private_key_int % CURVE.order
        raise KeyGenerationError, "Generated invalid private key" if private_key_int == 0
        
        # Generate public key from private key
        public_key_point = CURVE.generator * private_key_int
        
        # Convert to compressed public key format (33 bytes)
        public_key_compressed = ECDSA::Format::PointOctetString.encode(
          public_key_point, 
          compression: true
        )
        
        private_key_hex = private_key_int.to_s(16).rjust(64, '0')
        public_key_hex = public_key_compressed.unpack1('H*')
        
        KeyPair.new(private_key_hex, public_key_hex)
      rescue => e
        raise KeyGenerationError, "Failed to generate keys: #{e.message}"
      end
    end
    
    # Sign a message with a private key
    # @param message [String] The message to sign
    # @param private_key_hex [String] Private key as hex string
    # @param include_timestamp [Boolean] Whether to include timestamp in signature
    # @return [Signature] Signature object
    def self.sign(message, private_key_hex, include_timestamp: false)
      raise ArgumentError, "Message cannot be empty" if message.nil? || message.empty?
      raise ArgumentError, "Private key cannot be empty" if private_key_hex.nil? || private_key_hex.empty?
      
      begin
        # Add timestamp to message if requested
        signing_message = message
        timestamp = nil
        if include_timestamp
          timestamp = Time.now.to_f
          signing_message = "#{message}:#{timestamp}"
        end
        
        private_key_int = private_key_hex.to_i(16)
        raise SigningError, "Invalid private key" if private_key_int == 0
        
        message_hash = Digest::SHA256.digest(signing_message)
        
        # Generate deterministic k value (RFC 6979)
        k = generate_deterministic_k(private_key_int, message_hash)
        
        # Create signature
        signature = ECDSA.sign(CURVE, private_key_int, message_hash, k)
        
        # Encode signature in DER format
        der_signature = ECDSA::Format::SignatureDerString.encode(signature)
        signature_base64 = Base64.strict_encode64(der_signature)
        
        Signature.new(signature_base64, message, timestamp)
      rescue => e
        raise SigningError, "Failed to sign message: #{e.message}"
      end
    end
    
    private
    
    # Generate deterministic k value for ECDSA signing (RFC 6979)
    def self.generate_deterministic_k(private_key, message_hash)
      # Simplified deterministic k generation
      # In production, use proper RFC 6979 implementation
      combined = [private_key.to_s(16), message_hash.unpack1('H*')].join
      k_bytes = Digest::SHA256.digest(combined)
      k = k_bytes.unpack1('H*').to_i(16)
      k % CURVE.order
    end
  end
  
  class Server
    # Verify a signature with a public key
    # @param signature [String|Signature] Base64 encoded signature or Signature object
    # @param message [String] Original message that was signed (optional if Signature object provided)
    # @param public_key_hex [String] Public key as hex string
    # @param max_age_seconds [Integer] Maximum age of signature in seconds (nil for no limit)
    # @return [Boolean] True if signature is valid
    def self.verify_signature(signature, message = nil, public_key_hex, max_age_seconds: nil)
      begin
        # Handle both string and Signature object inputs
        if signature.is_a?(Signature)
          signature_base64 = signature.signature_base64
          original_message = message || signature.message
          timestamp = signature.timestamp
        else
          signature_base64 = signature
          original_message = message
          timestamp = nil
        end
        
        raise ArgumentError, "Message cannot be empty" if original_message.nil? || original_message.empty?
        raise ArgumentError, "Public key cannot be empty" if public_key_hex.nil? || public_key_hex.empty?
        
        # Check signature age if timestamp is available and max_age is specified
        if timestamp && max_age_seconds && (Time.now.to_f - timestamp) > max_age_seconds
          return false
        end
        
        # Reconstruct the signing message
        signing_message = timestamp ? "#{original_message}:#{timestamp}" : original_message
        
        # Decode signature
        der_signature = Base64.strict_decode64(signature_base64)
        signature_obj = ECDSA::Format::SignatureDerString.decode(der_signature)
        
        # Decode public key
        public_key_bytes = [public_key_hex].pack('H*')
        public_key_point = ECDSA::Format::PointOctetString.decode(
          public_key_bytes, 
          CURVE
        )
        
        # Hash message
        message_hash = Digest::SHA256.digest(signing_message)
        
        # Verify signature
        ECDSA.valid_signature?(public_key_point, message_hash, signature_obj)
      rescue => e
        # Any error in verification means invalid signature
        false
      end
    end
    
    # Generate a UUID v4 for user identification
    # @return [String] UUID v4 string
    def self.generate_uuid
      SecureRandom.uuid
    end
    
    # Associate two signatures to link public keys (convenience method)
    # @param signature1 [String|Signature] First signature
    # @param signature2 [String|Signature] Second signature  
    # @param message [String] Message that both signatures signed (optional for Signature objects)
    # @param public_key1_hex [String] First public key
    # @param public_key2_hex [String] Second public key
    # @return [Boolean] True if both signatures are valid
    def self.associate(signature1, signature2, message, public_key1_hex, public_key2_hex)
      verify_signature(signature1, message, public_key1_hex) &&
        verify_signature(signature2, message, public_key2_hex)
    end
  end
  
  # Utility methods
  class Utils
    # Validate that a string is a valid hex key
    def self.valid_hex_key?(hex_string, expected_length = nil)
      return false if hex_string.nil? || hex_string.empty?
      return false unless hex_string.match?(/\A[0-9a-fA-F]+\z/)
      return false if expected_length && hex_string.length != expected_length
      true
    end
    
    # Validate that a string is valid base64
    def self.valid_base64?(base64_string)
      return false if base64_string.nil? || base64_string.empty?
      Base64.strict_decode64(base64_string)
      true
    rescue ArgumentError
      false
    end
    
    # Generate a secure random seed phrase
    def self.generate_seed_phrase(words: 12)
      # Simple word list - in production use BIP39 word list
      word_list = %w[
        apple banana cherry date elderberry fig grape honeydew ice jackfruit
        kiwi lemon mango nectarine orange papaya quince raspberry strawberry
        tangerine ugli vanilla watermelon xigua yellow zucchini
      ]
      
      Array.new(words) { word_list.sample }.join(' ')
    end
  end
end

# Example usage and comprehensive tests
if __FILE__ == $0
  puts "Sessionless Protocol Ruby Implementation - Full Demo"
  puts "=" * 60
  
  begin
    # 1. Generate keys
    puts "\n1. Generating keys from seed phrase..."
    seed_phrase = "the quick brown fox jumps over the lazy dog"
    keys = Sessionless::Client.generate_keys(seed_phrase)
    
    puts "Private Key: #{keys.private_key_hex}"
    puts "Public Key:  #{keys.public_key_hex}"
    puts "Keys JSON:   #{keys.to_json}"
    
    # 2. Sign a message
    puts "\n2. Signing a message..."
    message = "Hello, Sessionless World! #{Time.now}"
    signature = Sessionless::Client.sign(message, keys.private_key_hex)
    
    puts "Message:   #{signature.message}"
    puts "Signature: #{signature.signature_base64}"
    puts "Timestamp: #{signature.timestamp}"
    
    # 3. Verify signature
    puts "\n3. Verifying signature..."
    is_valid = Sessionless::Server.verify_signature(
      signature.signature_base64, 
      signature.message, 
      keys.public_key_hex
    )
    
    puts "Valid signature: #{is_valid}"
    
    # 4. Sign with timestamp
    puts "\n4. Signing with timestamp..."
    timestamped_signature = Sessionless::Client.sign(
      message, 
      keys.private_key_hex, 
      include_timestamp: true
    )
    
    puts "Timestamped signature: #{timestamped_signature.to_json}"
    
    # 5. Verify timestamped signature
    puts "\n5. Verifying timestamped signature..."
    is_valid_timestamped = Sessionless::Server.verify_signature(
      timestamped_signature,
      nil,
      keys.public_key_hex,
      max_age_seconds: 3600 # 1 hour
    )
    
    puts "Valid timestamped signature: #{is_valid_timestamped}"
    
    # 6. Generate UUID
    puts "\n6. Generating UUID for user..."
    user_uuid = Sessionless::Server.generate_uuid
    puts "User UUID: #{user_uuid}"
    
    # 7. Test association
    puts "\n7. Testing key association..."
    keys2 = Sessionless::Client.generate_keys("another seed phrase")
    signature2 = Sessionless::Client.sign(message, keys2.private_key_hex)
    
    can_associate = Sessionless::Server.associate(
      signature, signature2, message,
      keys.public_key_hex, keys2.public_key_hex
    )
    
    puts "Can associate keys: #{can_associate}"
    
    # 8. Utility tests
    puts "\n8. Testing utilities..."
    puts "Valid hex key: #{Sessionless::Utils.valid_hex_key?(keys.private_key_hex, 64)}"
    puts "Valid base64: #{Sessionless::Utils.valid_base64?(signature.signature_base64)}"
    
    random_seed = Sessionless::Utils.generate_seed_phrase
    puts "Random seed phrase: #{random_seed}"
    
    # 9. Error handling demonstration
    puts "\n9. Demonstrating error handling..."
    begin
      Sessionless::Client.generate_keys("")
    rescue Sessionless::Error => e
      puts "Caught expected error: #{e.class} - #{e.message}"
    end
    
    puts "\n" + "=" * 60
    puts "Full demo completed successfully!"
    
  rescue => e
    puts "Error during demo: #{e.class} - #{e.message}"
    puts e.backtrace.first(5)
  end
end