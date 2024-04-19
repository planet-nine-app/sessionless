import { IsString, IsDate, IsObject } from "class-validator";
import { SignatureDto } from "./shared.dto";
export class CreateUserDto {
  @IsString()
  publicKey: string;
  @IsString()
  content: string;
  @IsDate()
  timestamp: Date;
  @IsObject()
  signature: SignatureDto;
}

