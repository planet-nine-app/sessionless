import { SignatureDto } from "./shared.dto";
import { IsString, IsDate, IsObject } from "class-validator";

export class ValidateMessageDto {
  @IsString()
  uuid: string;
  content: string;
  @IsDate()
  timestamp: Date;
  @IsObject()
  signature: SignatureDto;
}
