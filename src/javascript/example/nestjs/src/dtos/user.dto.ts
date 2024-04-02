import { IsString, IsDate, IsObject } from "class-validator";

export class CreateUserDto {
  @IsString()
  publicKey: string;
  @IsString()
  content: string;
  @IsDate()
  timestamp: Date;
  @IsObject()
  signature: Signature;
}

export class Signature {
  @IsString()
  r: string;
  @IsString()
  s: string;
}
